package tech.sabtih.forumapp.listeners;

import tech.sabtih.forumapp.models.user.User;
import tech.sabtih.forumapp.models.user.UserRank;

public interface OnProfileInteractionListener {
    public void onProfileLoaded(User user);
    void onRankInteraction(UserRank rank);
}
