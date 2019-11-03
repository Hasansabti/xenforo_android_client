package tech.sabtih.forumapp.listeners;

import tech.sabtih.forumapp.models.user.User;

public interface OnProfileInteractionListener {
    public void onProfileLoaded(User user);
}
