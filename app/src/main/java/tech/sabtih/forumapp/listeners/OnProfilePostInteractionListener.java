package tech.sabtih.forumapp.listeners;

import tech.sabtih.forumapp.models.Profilepost;
import tech.sabtih.forumapp.models.user.Simpleuser;

public interface OnProfilePostInteractionListener {
    public void onProfilePostInteraction(Profilepost profilepost);

    void viewPPComments(Profilepost mItem);

    void viewPPUser(Simpleuser mItem);
}
