package com.example.qklahpita.toeicvocab12.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qklahpita.toeicvocab12.R;
import com.example.qklahpita.toeicvocab12.databases.DatabaseManager;
import com.example.qklahpita.toeicvocab12.databases.models.TopicModel;
import com.example.qklahpita.toeicvocab12.databases.models.WordModel;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudyActivity extends AppCompatActivity {
    private static final String TAG = "StudyActivity";

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_name_topic)
    TextView tvNameTopic;
    @BindView(R.id.tv_origin)
    TextView tvOrigin;
    @BindView(R.id.tv_pronun)
    TextView tvPronun;
    @BindView(R.id.tv_details)
    TextView tvDetails;
    @BindView(R.id.tv_explain)
    TextView tvExplain;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.iv_word)
    ImageView ivWord;
    @BindView(R.id.tv_example)
    TextView tvExample;
    @BindView(R.id.tv_example_trans)
    TextView tvExampleTrans;
    @BindView(R.id.tv_didnt_know)
    TextView tvDidntKnow;
    @BindView(R.id.tv_knew)
    TextView tvKnew;
    @BindView(R.id.cl_detail_part)
    ConstraintLayout clDetailPart;
    @BindView(R.id.cv_word)
    CardView cvWord;
    @BindView(R.id.rl_background)
    RelativeLayout rlBackground;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.cl_full)
    ConstraintLayout clFull;

    WordModel wordModel;
    int preId = -1;
    AnimatorSet animatorSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        ButterKnife.bind(this);

        setupUI();
        loadData();
    }

    private void loadData() {
        TopicModel topicModel = (TopicModel) getIntent().getSerializableExtra("topic");
        tvNameTopic.setText(topicModel.name);
        rlBackground.setBackgroundColor(Color.parseColor(topicModel.color));

        wordModel = DatabaseManager.getInstance(this).getRandomWord(topicModel.id, preId);
        preId = wordModel.id;

        tvOrigin.setText(wordModel.origin);
        tvPronun.setText(wordModel.pronunciation);
        tvType.setText(wordModel.type);
        tvExplain.setText(wordModel.explanation);
        tvExample.setText(wordModel.example);
        tvExampleTrans.setText(wordModel.example_trans);

        Picasso.get().load(wordModel.imageUrl).into(ivWord);

        switch (wordModel.level) {
            case 0:
                tvLevel.setText("New word");
                break;
            case 1:
            case 2:
            case 3:
                tvLevel.setText("Review");
                break;
            case 4:
                tvLevel.setText("Master");
                break;
        }
    }

    private void setupUI() {
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
    }

    @OnClick({R.id.iv_back, R.id.tv_details, R.id.tv_didnt_know, R.id.tv_knew})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_details:
                clFull.setLayoutTransition(new LayoutTransition());
                changeContent(false);
                break;
            case R.id.tv_didnt_know:
                nextWord(false);
                break;
            case R.id.tv_knew:
                nextWord(true);
                break;
        }
    }

    public void changeContent(boolean isExpanded) {
        if (isExpanded) {
            clDetailPart.setVisibility(View.GONE);
            tvDetails.setVisibility(View.VISIBLE);
        } else {
            clDetailPart.setVisibility(View.VISIBLE);
            tvDetails.setVisibility(View.GONE);
        }
    }

    public void nextWord(final boolean isKnown) {
        setAnimation(R.animator.animation_move_to_left);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                DatabaseManager.getInstance(StudyActivity.this).updateWordLevel(wordModel, isKnown);
                loadData();

                clFull.setLayoutTransition(null);

                changeContent(true);
                setAnimation(R.animator.animation_move_from_right);
            }
        });
    }

    public void setAnimation(int animation) {
        animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, animation);
        animatorSet.setTarget(cvWord);
        animatorSet.start();
    }
}
