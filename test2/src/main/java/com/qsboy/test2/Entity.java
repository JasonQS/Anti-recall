package lee.com.test;

import java.util.List;

/**
 * Description：
 * Author：Lee
 * Date：2017/6/22 17:03
 */

public class Entity {

    public String title;
    public List<InnerEntity> innerEntities;

    public int scrollOffset;
    public int scrollPosition;

    public static class InnerEntity {
        public String innerTitle;
        public int innerImageId;

        public InnerEntity(String innerTitle, int innerImageId) {
            this.innerTitle = innerTitle;
            this.innerImageId = innerImageId;
        }
    }

    public Entity(String title, List<InnerEntity> innerEntities) {
        this.title = title;
        this.innerEntities = innerEntities;
    }

    public Entity() {
    }
}
