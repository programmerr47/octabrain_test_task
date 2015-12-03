package com.github.programmerr47.imageviewer.representation.tasks;


import com.github.programmerr47.imageviewer.api.ApiGetMethod;
import com.github.programmerr47.imageviewer.api.ApiMethod;
import com.github.programmerr47.imageviewer.api.parsers.htmlparsers.PhotosParser;
import com.github.programmerr47.imageviewer.representation.adapters.items.PhotoItem;

import java.util.List;

/**
 * @author Michael Spitsin
 * @since 2014-09-13
 */
public class GetImagesTask extends AsyncTaskWithListener<String, Void, List<PhotoItem>> {

    @Override
    protected final List<PhotoItem> doInBackground(String... params) {
        if (params.length > 0) {
            ApiMethod<List<PhotoItem>> request = initApiMethod(params[0]);

            if (request != null) {
                try {
                    return request.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private ApiMethod<List<PhotoItem>> initApiMethod(String query) {
        return new ApiGetMethod<List<PhotoItem>>()
                .setUrl("https://yandex.ru/images/search?text=" + query)
                .setMethodResultParser(new PhotosParser());
    }
}
