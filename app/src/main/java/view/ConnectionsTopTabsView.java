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

public class ConnectionsTopTabsView extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private View viewLocation;
    private View viewAdd;

    private ImageView imageLocation;
    private ImageView imageAdd;

    private ArgbEvaluator connectionsAgbEvaluator;

    private int onColor;
    private int offColor;

    public ConnectionsTopTabsView(Context context) {
        this(context, null);
    }

    public ConnectionsTopTabsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConnectionsTopTabsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void setUpWithViewPager(ViewPager viewPager){
        viewPager.addOnPageChangeListener(this);

        viewLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 0)
                    viewPager.setCurrentItem(0);
            }
        });

        viewAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 1)
                    viewPager.setCurrentItem(1);
            }
        });
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_connections_top_tabs, this, true);

        viewLocation = (View) findViewById(R.id.view_location);
        viewAdd = (View) findViewById(R.id.view_add);

        imageLocation = (ImageView) findViewById(R.id.image_location);
        imageAdd = (ImageView) findViewById(R.id.image_add);

        connectionsAgbEvaluator = new ArgbEvaluator();

        onColor = ContextCompat.getColor(getContext(), R.color.connect);
        offColor = ContextCompat.getColor(getContext(), R.color.white);

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
        int imageOnColor = (int) connectionsAgbEvaluator.evaluate(fractionFromCenter, onColor, offColor);
        int imageOffColor = (int) connectionsAgbEvaluator.evaluate(fractionFromCenter, offColor, onColor);

        imageLocation.setColorFilter(imageOnColor);
        imageAdd.setColorFilter(imageOffColor);
    }
}