package com.monil20.resolve;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class Home extends Activity implements View.OnTouchListener
{
    int userId;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        userId = Integer.parseInt(getIntent().getStringExtra("userId"));
        Toast.makeText(getApplicationContext(),userId+"", Toast.LENGTH_SHORT).show();

        ImageView iv = findViewById (R.id.image);
        if (iv != null) {
            iv.setOnTouchListener (this);
        }

        //toast ("Touch the screen to discover where the regions are.");
    }

    public boolean onTouch (View v, MotionEvent ev)
    {
        boolean handledHere = false;

        final int action = ev.getAction();

        final int evX = (int) ev.getX();
        final int evY = (int) ev.getY();
        int nextImage = -1;			// resource id of the next image to display

        // If we cannot find the imageView, return.
        ImageView imageView = (ImageView) v.findViewById (R.id.image);
        if (imageView == null) return false;

        // When the action is Down, see if we should show the "pressed" image for the default image.
        // We do this when the default image is showing. That condition is detectable by looking at the
        // tag of the view. If it is null or contains the resource number of the default image, display the pressed image.
        Integer tagNum = (Integer) imageView.getTag ();
        int currentResource = (tagNum == null) ? R.drawable.homescreen : tagNum.intValue ();

        // Now that we know the current resource being displayed we can handle the DOWN and UP events.

        switch (action) {
            case MotionEvent.ACTION_DOWN :
                if (currentResource == R.drawable.homescreen) {
                    nextImage = R.drawable.homescreen;
                    handledHere = true;
       /*
       } else if (currentResource != R.drawable.p2_ship_default) {
         nextImage = R.drawable.p2_ship_default;
         handledHere = true;
       */
                } else handledHere = true;
                break;

            case MotionEvent.ACTION_UP :
                // On the UP, we do the click action.
                // The hidden image (image_areas) has three different hotspots on it.
                // The colors are red, blue, and yellow.
                // Use image_areas to determine which region the user touched.
                int touchColor = getHotspotColor (R.id.image_areas, evX, evY);

                // Compare the touchColor to the expected values. Switch to a different image, depending on what color was touched.
                // Note that we use a Color Tool object to test whether the observed color is close enough to the real color to
                // count as a match. We do this because colors on the screen do not match the map exactly because of scaling and
                // varying pixel density.
                ColorTool ct = new ColorTool ();
                int tolerance = 25;
                nextImage = R.drawable.homescreen;
                if (ct.closeMatch (Color.RED, touchColor, tolerance)) toast ("Photo Frame Touched") ;
                else if (ct.closeMatch (Color.rgb(4,46,167), touchColor, tolerance)) toast ("Desktop clicked");
                else if (ct.closeMatch (Color.GREEN, touchColor, tolerance)) {
                    Intent intent = new Intent(getApplicationContext(),SubmitIssue.class);
                    intent.putExtra("userId",userId+"");
                    startActivity(intent);
                }
                else if (ct.closeMatch (Color.BLACK, touchColor, tolerance)) {
                    Intent intent = new Intent(getApplicationContext(),MyCases.class);
                    intent.putExtra("userId",userId+"");
                    startActivity(intent);
                }
                else if (ct.closeMatch (Color.GRAY, touchColor, tolerance)) toast ("Notification");

                // If the next image is the same as the last image, go back to the default.
                // toast ("Current image: " + currentResource + " next: " + nextImage);
                if (currentResource == nextImage) {
                    nextImage = R.drawable.homescreen;
                }
                handledHere = true;
                break;

            default:
                handledHere = false;
        } // end switch

        /*if (handledHere) {

            if (nextImage > 0) {
                imageView.setImageResource (nextImage);
                imageView.setTag (nextImage);
            }
        }*/
        return handledHere;
    }

    /*@Override protected void onResume() {
        super.onResume();

        View v  = findViewById (R.id.wglxy_bar);
        if (v != null) {
            Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            //anim1.setAnimationListener (new StartActivityAfterAnimation (i));
            v.startAnimation (anim1);
        }
    }*/

    /**
     * Handle a click on the Wglxy views at the bottom.
     *
     */

    /*public void onClickWglxy (View v) {
        Intent viewIntent = new Intent ("android.intent.action.VIEW",
                Uri.parse("http://double-star.appspot.com/blahti/ds-download.html"));
        startActivity(viewIntent);

    }*/


/**
 */
// More methods

    /**
     * Get the color from the hotspot image at point x-y.
     *
     */

    public int getHotspotColor (int hotspotId, int x, int y) {
        ImageView img = (ImageView) findViewById (hotspotId);
        if (img == null) {
            Log.d ("ImageAreasActivity", "Hot spot image not found");
            return 0;
        } else {
            img.setDrawingCacheEnabled(true);
            Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
            if (hotspots == null) {
                Log.d ("ImageAreasActivity", "Hot spot bitmap was not created");
                return 0;
            } else {
                img.setDrawingCacheEnabled(false);
                return hotspots.getPixel(x, y);
            }
        }
    }

    /**
     * Show a string on the screen via Toast.
     *
     * @param msg String
     * @return void
     */

    public void toast (String msg)
    {
        Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
    } // end toast
}
