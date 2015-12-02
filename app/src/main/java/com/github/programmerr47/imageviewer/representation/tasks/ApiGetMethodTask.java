package com.github.programmerr47.imageviewer.representation.tasks;


import com.github.programmerr47.imageviewer.api.ApiMethod;

/**
 * @author Michael Spitsin
 * @since 2014-09-13
 */
public class ApiGetMethodTask<Response> extends AsyncTaskWithListener<ApiMethod<Response>, Void, Response> {

    @SafeVarargs
    @Override
    protected final Response doInBackground(ApiMethod<Response>... params) {
        if (params.length > 0) {
            ApiMethod<Response> request = params[0];

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
}
