package tech.sabtih.forumapp.listeners;

import tech.sabtih.forumapp.dummy.DummyContent;
import tech.sabtih.forumapp.models.Threadreply;
import tech.sabtih.forumapp.models.user.Simpleuser;

public interface OnTReplyInteractionListener {
    // TODO: Update argument type and name
    void onListFragmentInteraction(DummyContent.DummyItem item);

    void onTReplyInteraction(Threadreply mItem);

    void viewUser(Simpleuser su);
}
