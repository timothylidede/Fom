package view;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.savala.fom.R;

public class ActivityTopTabsView extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private View viewMe;
    private View viewPacks;

    private ImageView imageMe;
    private ImageView imagePacks;

    private ArgbEvaluator activityAgbEvaluator;

    private int onColor;
    private int offColor;

    public ActivityTopTabsView(Context context) {
        this(context, null);
    }

    public ActivityTopTabsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActivityTopTabsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void setUpWithViewPager(ViewPager viewPager){
        viewPager.addOnPageChangeListener(this);

        viewMe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 0)
                    viewPager.setCurrentItem(0);
            }
        });

        viewPacks.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 1)
                    viewPager.setCurrentItem(1);
            }
        });
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_activity_top_tabs, this, true);

        viewMe = (View) findViewById(R.id.view_me);
        viewPacks = (View) findViewById(R.id.view_packs);

        imageMe = (ImageView) findViewById(R.id.image_me);
        imagePacks = (ImageView) findViewById(R.id.image_packs);

        activityAgbEvaluator = new ArgbEvaluator();

        onColor = ContextCompat.getColor(getContext(), R.color.white);
        offColor = ContextCompat.getColor(getContext(), R.color.activity);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(position == 0){
            setColor(positionOffset);
        }else{
            setColor(1-positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setColor(float fractionFromCenter){
        int imageOnColor = (int) activityAgbEvaluator.evaluate(fractionFromCenter, onColor, offColor);
        int imageOffColor = (int) activityAgbEvaluator.evaluate(fractionFromCenter, offColor, onColor);

        imageMe.setColorFilter(imageOnColor);
        imagePacks.setColorFilter(imageOffColor);
    }
}
