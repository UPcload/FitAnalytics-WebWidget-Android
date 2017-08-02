import java.util.List;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.Rule;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.rule.ActivityTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import com.fitanalytics.TestRunner.MainActivity;
import com.fitanalytics.TestRunner.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.Promise;

import com.fitanalytics.webwidget.WidgetOptions;
import com.fitanalytics.webwidget.ManufacturedSize;
import com.fitanalytics.TestRunner.FITAWebWidgetDriver;

import android.util.Log;

@RunWith(AndroidJUnit4.class)
public class BasicTest {
    private MainActivity mainActivity;
    protected FITAWebWidgetDriver widget;


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void setActivity() {
        mainActivity = mActivityRule.getActivity();
    }

    private Promise createAndLoadWidget() {
        return mainActivity.initializeWidget()
        .then(new DonePipe() {
            public Promise pipeDone(Object result) {
                widget = mainActivity.getWidget();
                return mainActivity.widgetLoad();
            }
        });
    }

    private Promise createAndInitDriver() {
        return createAndLoadWidget()
        .then(new DonePipe() {
            public Promise pipeDone(Object result) {
                return mainActivity.initializeDriver();
            }
        });
    }

    private Promise createAndConfigureWidget(final String productSerial, final WidgetOptions options) {
        return createAndInitDriver()
        .then(new DonePipe() {
            public Promise pipeDone(Object result) {
                return mainActivity.widgetCreate(productSerial, options);
            }
        });
    }

    public void logTestName() {
        Log.d("fitaWidget", Thread.currentThread().getStackTrace()[3].getMethodName());
    }

    @Test
    public void testWebViewShouldExist() throws Exception {
        logTestName();
        onView(withId(R.id.widget_webview)).check(matches(isDisplayed()));
        Log.d("fitaWidget", "TEST webview exists");
    }

    @Test
    public void testWidgetInitializer() throws Exception {
        logTestName();
        final Object sync = new Object();

        mainActivity.initializeWidget()
        .then(new DoneCallback() {
            public void onDone(Object result) {
                Log.d("fitaWidget", "TEST widget init, " + result.toString());  
                synchronized (sync) { sync.notify(); }
            }
        });

        synchronized (sync) { sync.wait(); }
    }

    @Test
    public void testWidgetLoad() throws Exception {
        logTestName();
        final Object sync = new Object();
        createAndLoadWidget()
        .done(new DoneCallback() {
            public void onDone(Object result) {
                Log.d("fitaWidget", "TEST load " + result.toString());  
                synchronized (sync) { sync.notify(); }
            }
        });
        synchronized (sync) { sync.wait(); }
    }

    @Test
    public void testJavascriptEval() throws Exception {
        logTestName();
        final Object sync = new Object();
        createAndLoadWidget()
        .then(new DonePipe() {
            public Promise pipeDone(Object result) { 
                return mainActivity.evaluateJavaScriptAsync("1 + 1");
            }
        })
        .done(new DoneCallback() {
            public void onDone(Object result) {
                Log.d("fitaWidget", "TEST eval " + result.toString());  
                synchronized (sync) { sync.notify(); }
            }
        });
        synchronized (sync) { sync.wait(); }
    }

    @Test
    public void testDriverInstall() throws Exception {
        logTestName();
        final Object sync = new Object();
        createAndInitDriver()
        .then(new DonePipe() {
            public Promise pipeDone(Object result) { 
                return mainActivity.evaluateJavaScriptAsync("JSON.stringify(window.__driver)");
            }
        })
        .then(new DonePipe() {
            public Promise pipeDone(Object result) { 
                assertThat(result.toString(), is("\"{}\""));
                return mainActivity.evaluateJavaScriptAsync("window.__driver.works()");
            }
        })
        .then(new DonePipe() {
            public Promise pipeDone(Object result) { 
                assertThat(result.toString(), is("true"));
                return mainActivity.evaluateJavaScriptAsync("Boolean(window.__widgetManager)");
            }
        })
        .then(new DoneCallback() {
            public void onDone(Object result) {
                Log.d("fitaWidget", "TEST manager " + result.toString());  
                assertThat(result.toString(), is("true"));
                synchronized (sync) { sync.notify(); }
            }
        });
        synchronized (sync) { sync.wait(); }
    }

    @Test
    public void testWidgetCreate() throws Exception {
        logTestName();
        final Object sync = new Object();
        createAndInitDriver()
        .then(new DonePipe() {
            public Promise pipeDone(Object result) { 
                return mainActivity.widgetCreate("upcload-XX-test", new WidgetOptions());
            }
        })
        .then(new DoneCallback() {
            public void onDone(Object result) {
                JSONObject res = (JSONObject) result;
                assertThat(res.optString("productSerial"), is("upcload-XX-test"));
                Log.d("fitaWidget", "TEST widget load " + result.toString());  
                synchronized (sync) { sync.notify(); }
            }
        });
        synchronized (sync) { sync.wait(); }
    }

    @Test
    public void testWidgetCreateAndOpen() throws Exception {
        logTestName();
        final Object sync = new Object();
        createAndInitDriver()
        .then(new DonePipe() { public Promise pipeDone(Object result) { 
            return mainActivity.widgetCreate("upcload-XX-test", new WidgetOptions());
        }})
        .then(new DonePipe() { public Promise pipeDone(Object result) {
            JSONObject res = (JSONObject) result;
            assertThat(res.optString("productSerial"), is("upcload-XX-test"));
            return mainActivity.widgetOpen(null, new WidgetOptions());
        }})
        .then(new DonePipe() { public Promise pipeDone(Object result) {
            // wait for the widget to catch up a bit
            try { Thread.sleep(2000); } catch (Exception e) {};
            return widget.testGetScreenName();
        }})
        .then(new DoneCallback() { public void onDone(Object result) {
           Log.d("fitaWidget", "TEST widget open " + result.toString());  
            assertThat(result.toString(), is("\"lower_form_sp\""));
            synchronized (sync) { sync.notify(); }
        }});
        synchronized (sync) { sync.wait(); }
    }

    @Test(timeout=4000)
    public void testWidgetCreateWithSerialAndRecommend() throws Exception {
        logTestName();
        final Object sync = new Object();
        createAndInitDriver()
        .then(new DonePipe() { public Promise pipeDone(Object result) { 
            return mainActivity.widgetCreate("upcload-XX-test", new WidgetOptions());
        }})
        .then(new DonePipe() { public Promise pipeDone(Object result) {
            JSONObject res = (JSONObject) result;
            assertThat(res.optString("productSerial"), is("upcload-XX-test"));
            return mainActivity.widgetRecommend(null, null);
        }})
        .then(new DoneCallback() { public void onDone(Object result) {
            Log.d("fitaWidget", "TEST2 widget recommendation " + result.toString());  
            synchronized (sync) { sync.notify(); }
        }});
        synchronized (sync) { sync.wait(); }
    }

    @Test(timeout=4000)
    public void testWidgetCreateAndRecommendWithSerial() throws Exception {
        logTestName();
        final Object sync = new Object();
        createAndInitDriver()
        .then(new DonePipe() { public Promise pipeDone(Object result) { 
            return mainActivity.widgetCreate(null, null);
        }})
        .then(new DonePipe() { public Promise pipeDone(Object result) {
            JSONObject res = (JSONObject) result;
            return mainActivity.widgetRecommend("upcload-XX-test", new WidgetOptions());

        }})
        .then(new DoneCallback() { public void onDone(Object result) {
            Log.d("fitaWidget", "widget recommendation " + result.toString());  
            synchronized (sync) { sync.notify(); }
        }});
        synchronized (sync) { sync.wait(); }
    }

    @Test(timeout=4000)
    public void testWidgetCreateAndReconfigureWithSerialAndRecommend() throws Exception {
        logTestName();
        final Object sync = new Object();
        createAndInitDriver()
        .then(new DonePipe() { public Promise pipeDone(Object result) { 
            return mainActivity.widgetCreate(null, null);
        }})
        .then(new DonePipe() { public Promise pipeDone(Object result) { 
            return mainActivity.widgetReconfigure("upcload-XX-test", new WidgetOptions());
        }})
        .then(new DonePipe() { public Promise pipeDone(Object result) {
            JSONObject res = (JSONObject) result;
            assertThat(res.optString("productSerial"), is("upcload-XX-test"));
            return mainActivity.widgetRecommend(null, null);
        }})
        .then(new DoneCallback() { public void onDone(Object result) {
            Log.d("fitaWidget", "widget recommendation " + result.toString());  
            synchronized (sync) { sync.notify(); }
        }});
        synchronized (sync) { sync.wait(); }
    }

    @Test
    public void testWidgeCreateOptions() throws Exception {
        logTestName();
        final Object sync = new Object();
        String[] sizes = { "S", "M", "L" };

        List<ManufacturedSize> msizes = new ArrayList<>();
        msizes.add(new ManufacturedSize("S", true));
        msizes.add(new ManufacturedSize("M", true));
        msizes.add(new ManufacturedSize("L", true));
        msizes.add(new ManufacturedSize("XL", false));

        WidgetOptions options = new WidgetOptions()
            .setLanguage("pt")
            .setShopCountry("BR")
            .setUserId("user1234")
            .setThumb("https://widget.fitanalytics.com/images/integration/fit-finder-retina.png")
            .setSizes(sizes)
            .setManufacturedSizes(msizes)
            .setCartEnabled(true);

        createAndConfigureWidget("upcload-XX-test", options)
        .then(new DonePipe() { public Promise pipeDone(Object result) {
            return widget.testGetWidgetOptionValue("language");
        }})
        .then(new DonePipe() { public Promise pipeDone(Object result) {
            Log.d("fitaWidget", "option language " + result.toString());
            assertThat(result.toString(), is("\"pt\""));
            return widget.testGetWidgetOptionValue("shopCountry");
        }})
        .then(new DonePipe() { public Promise pipeDone(Object result) {
            Log.d("fitaWidget", "option shopCountry " + result.toString());
            assertThat(result.toString(), is("\"BR\""));
            return widget.testGetWidgetOptionValue("userId");
        }})
        .then(new DonePipe() { public Promise pipeDone(Object result) {
            Log.d("fitaWidget", "option userId " + result.toString());
            assertThat(result.toString(), is("\"user1234\""));
            return widget.testGetWidgetOptionValue("thumb");
        }})
        .then(new DonePipe() { public Promise pipeDone(Object result) {
            Log.d("fitaWidget", "option thumb " + result.toString());
            assertThat(result.toString(), is("\"https://widget.fitanalytics.com/images/integration/fit-finder-retina.png\""));
            return widget.testGetWidgetOptionValue("sizes");
        }})
        .then(new DonePipe() { public Promise pipeDone(Object result) {
            Log.d("fitaWidget", "option sizes " + result.toString());
            assertThat(result.toString(), is("[\"S\",\"M\",\"L\"]"));
            return widget.testGetWidgetOptionValue("manufacturedSizes");
        }})
        .then(new DonePipe() { public Promise pipeDone(Object result) {
            Log.d("fitaWidget", "option manufacturedSizes " + result.toString());
            assertThat(result.toString(), is("[{\"S\":true},{\"M\":true},{\"L\":true},{\"XL\":false}]"));
            return widget.testGetWidgetOptionType("cart");
        }})
        .then(new DoneCallback() { public void onDone(Object result) {
            Log.d("fitaWidget", "option cart type " + result.toString());
            assertThat(result.toString(), is("\"Function\""));
            synchronized (sync) { sync.notify(); }
        }});
        synchronized (sync) { sync.wait(); }
    }
}