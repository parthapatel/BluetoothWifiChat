package info.devexchanges.bluetoothchatapp.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import info.devexchanges.bluetoothchatapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_INET_ADDRESS = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText editText;
    private ListView listView;
    private Button button;
    private ArrayAdapter arrayAdapter;
    private List<String> messages;

    // TODO: Rename and change types of parameters
    private InetAddress inetAddress;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(InetAddress param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INET_ADDRESS, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            inetAddress = (InetAddress) getArguments().getSerializable(ARG_INET_ADDRESS);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wi_fi_direct_chat, container, false);
        editText = (EditText) view.findViewById(R.id.etMessage);
        listView = (ListView) view.findViewById(R.id.list);
        button = (Button) view.findViewById(R.id.sendButton);

        messages = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, messages);
        listView.setAdapter(arrayAdapter);

        new Thread(new ClientThread()).start();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class ClientThread implements Runnable {

        @Override
        public void run() {
            try {
                Socket socket = new Socket();
                socket.bind(null);
                socket.connect(new InetSocketAddress(inetAddress, 8888), 500);

                try(BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true)) {

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String data = editText.getText().toString();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    printWriter.println(data);
                                }
                            }).start();
                            editText.setText("");
                        }
                    });

                    while (true) {
                        final String data = is.readLine();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messages.add(data);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } finally {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
