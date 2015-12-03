package com.github.programmerr47.imageviewer.api.parsers.htmlparsers;

import android.net.Uri;

import com.github.programmerr47.imageviewer.representation.adapters.items.PhotoItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

/**
 * @author Michael Spitsin
 * @since 2015-08-06
 */
public class SinglePhotoParser extends HTMLParser<PhotoItem> {

    private static final String DATA_BEM_ATTR = "data-bem";
    private static final String SERP_ITEM = "serp-item";
    private static final String THUMB = "thumb";
    private static final String SNIPPET = "snippet";
    private static final String URL = "url";
    private static final String TEXT = "text";

    @Override
    protected PhotoItem parseObjectFromDoc(Element element) {
        String json = element.attr(DATA_BEM_ATTR);
        JSONObject jsonObject = getJsonObject(json);

        if (jsonObject != null) {
            String url = jsonObject.optJSONObject(SERP_ITEM).optJSONObject(THUMB).optString(URL);
            String shortDescr = jsonObject.optJSONObject(SERP_ITEM).optJSONObject(SNIPPET).optString(TEXT);

            if (!Uri.parse(url).toString().isEmpty()) {
                return PhotoItem.createInstance(Uri.parse(url), shortDescr);
            }
        }

        return null;
    }

    private JSONObject getJsonObject(String json) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
