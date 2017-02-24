/*
 * ACDD Project
 * file BundleParser.java  is  part of ACCD
 * The MIT License (MIT)  Copyright (c) 2015 Bunny Blue,achellies.
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */
package org.acdd.android.initializer;

import android.content.Context;
import android.util.Log;

import org.acdd.bundleInfo.BundleInfoList;
import org.acdd.bundleInfo.BundleInfoList.BundleInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * BundleParser  parser  bundle  list info from json
 * @author BunnyBlue
 *
 */
public class BundleParser {
    private static final String TAG = "BundleParser";
    public static void parser(Context mContext) {
        Log.d(TAG, "parser() called with: mContext = [" + mContext + "]");
        InputStream is;
        ArrayList<BundleInfo> bundleInfos = new ArrayList<BundleInfo>();
        try {
            is = mContext.getAssets().open("bundle-info.json");
            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            JSONArray jsonArray = new JSONArray(new String(buffer));
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject tmp = jsonArray.optJSONObject(index);
                BundleInfo mBundleInfo = new BundleInfo();

                mBundleInfo.bundleName = tmp.optString("pkgName");
                mBundleInfo.hasSO = tmp.optBoolean("hasSO");

                ArrayList<String> components = new ArrayList<String>();

                JSONArray activities = tmp.optJSONArray("activities");
                for (int j = 0; j < activities.length(); j++) {
                    components.add(activities.getString(j));

                }

                JSONArray receivers = tmp.optJSONArray("receivers");
                for (int j = 0; j < receivers.length(); j++) {
                    components.add(receivers.getString(j));

                }

                JSONArray services = tmp.optJSONArray("services");
                for (int j = 0; j < services.length(); j++) {
                    components.add(services.getString(j));
                }

                JSONArray contentProviders = tmp.optJSONArray("contentProviders");
                for (int j = 0; j < contentProviders.length(); j++) {
                    components.add(contentProviders.getString(j));

                }

                JSONArray dependencys = tmp.optJSONArray("dependency");
                for (int j = 0; j < dependencys.length(); j++) {
                    mBundleInfo.DependentBundles.add(dependencys.getString(j));
                }
                mBundleInfo.Components = components;
                bundleInfos.add(mBundleInfo);

            }
            BundleInfoList.getInstance().init(bundleInfos);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
