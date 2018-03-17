package lee.com.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HorizontalActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private HorizontalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical);
        ButterKnife.bind(this);

        List<Entity> list = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            Entity entity = new Entity();
            entity.title = "title" + i;
            List<Entity.InnerEntity> innerEntities = new ArrayList<>();
            for (int j = 1; j < 11; j++) {
                innerEntities.add(new Entity.InnerEntity(("Inner Title" + i + " - " + j), j % 3 == 0 ? R.mipmap.ic_launcher_round : R.mipmap.ic_launcher));
            }
            entity.innerEntities = innerEntities;
            list.add(entity);
        }

        adapter = new HorizontalAdapter(list);
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
    }
}
