package cabiso.daphny.com.g_companion.notifications;

/**
 * Created by cicctuser on 8/22/2018.
 */

public class Notification {
    private String notificationId;
    private String postId;
    private String actorName;
    private String actorImage;
    private String notiifcationActionContent;
    private String type;
    private String date;
    private boolean opened;

    public Notification() {
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorImage() {
        return actorImage;
    }

    public void setActorImage(String actorImage) {
        this.actorImage = actorImage;
    }

    public String getNotiifcationActionContent() {
        return notiifcationActionContent;
    }

    public void setNotiifcationActionContent(String notiifcationActionContent) {
        this.notiifcationActionContent = notiifcationActionContent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}