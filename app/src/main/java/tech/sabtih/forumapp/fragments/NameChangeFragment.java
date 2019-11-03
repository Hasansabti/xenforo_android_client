package tech.sabtih.forumapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.adapters.ProfileAboutListAdapter;
import tech.sabtih.forumapp.models.user.NameChange;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnNameChangeInteractionListener}
 * interface.
 */
public class NameChangeFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnNameChangeInteractionListener mListener;
    private ArrayList<NameChange> nameChanges;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NameChangeFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NameChangeFragment newInstance(ArrayList<NameChange> changes) {
        NameChangeFragment fragment = new NameChangeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, changes.size());
        fragment.setArguments(args);
        fragment.setNameChanges(changes);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_namechange_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
          //  if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
          //  } else {
              //  recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
         //   }
            recyclerView.setAdapter(new ProfileAboutListAdapter(nameChanges, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNameChangeInteractionListener) {
            mListener = (OnNameChangeInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNameChangeInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setNameChanges(ArrayList<NameChange> nameChanges) {
        this.nameChanges = nameChanges;
    }

    public ArrayList<NameChange> getNameChanges() {
        return nameChanges;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnNameChangeInteractionListener {
        // TODO: Update argument type and name
        void onNameChangeInteraction(NameChange item);
    }
}
