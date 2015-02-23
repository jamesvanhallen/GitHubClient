package com.james.testgithub.main.Another;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;


public class GitHubLoader {

    private static final String TAG = "sm_GitHubAPI";
    public static final String VAR_TOKEN = "token";
    public static final String API_URL = "https://api.github.com";
    private static final String HEADER_LINK = "Link";
    public static final String PREFERENCES = "basepreffile";
    public static final String AUTH_NOTE = "Simple GitHub Client Test App";
    public static final String[] SCOPES = new String[] {TokenRequest.SCOPE_REPO};

    public static final int ERR_CODE_UNAUTH = 401;
    public static final int ERR_CODE_FORBIDDEN = 403;
    public static final int ERR_CODE_NOT_FOUND = 404;
    public static final int ERR_CODE_CONFLICT = 409;
    public static final int ERR_CODE_UNKONWN_ERROR = 666;
    public static final int ERR_CODE_NO_INTERNET = 665;
    public static final int ERR_CODE_SERVICE_UNAVAILABLE = 667;

    private static GitHubLoader sInstance;
    private Context mContext;
    private GitHubInterface mService;
    private String mToken;
    private User mUserInfo;
    private Response mLastResponseHeaders;


    private GitHubLoader(Context context){

        mContext = context;

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                if(mToken !=null) {
                    request.addHeader("Authorization", "token " + mToken);
                }
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(GitHubLoader.API_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();
        mService = restAdapter.create(GitHubInterface.class);
        if(mContext !=null) {
            if (mToken == null) {
                mToken = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(VAR_TOKEN, null);
            }
        }
    }

    public static GitHubLoader getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new GitHubLoader(context);
        }
        return sInstance;
    }

    public static GitHubLoader getInstance() {
        return sInstance;
    }

    public void getBasicUserInfo(final RequestCallback<User> cb) {

        mService.getBasicUserInfo(new Callback<User>() {
            @Override
            public void success(User userInfo, Response response2) {
                mLastResponseHeaders = response2;
                mUserInfo = userInfo;
                cb.onSuccess(userInfo);
            }

            @Override
            public void failure(RetrofitError error) {

                defineError(error, cb);
            }
        });
    }

    public void getAuthorization(final String login, final String password, final RequestCallback<User> cb) {
        mToken = null;
        int page = 1;
        try {
            String lgnpsw = login+":"+password;
            final String encodedAuthString = "Basic "+new String( Base64.encode( lgnpsw.getBytes("UTF-8"), Base64.NO_WRAP), "UTF-8" );
            mService.checkAuth(encodedAuthString, page, new Callback<List<AuthorizationResult>>() {

                @Override
                public void success(List<AuthorizationResult> authorizationsList, Response response) {
                    mLastResponseHeaders = response;
                    if (authorizationsList != null && !authorizationsList.isEmpty()) {
                        for (AuthorizationResult auth : authorizationsList) {
                           if (auth.note != null && auth.note.equals(AUTH_NOTE) && auth.scopes != null &&
                                   auth.scopes.length == SCOPES.length) {
                                if (Arrays.equals(auth.scopes, SCOPES)) {
                                    mToken = auth.token;
                                    break;
                                }
                            }
                        }
                    }

                    if (mToken != null) {
                        writeToken();
                        getBasicUserInfo(cb);
                    }   else if(hasLastResponseNextPage()) {
                        int nextPage = getNextPageFromLastResponse();
                        mService.checkAuth(encodedAuthString, nextPage, this);
                    }   else {
                        createToken(login, password, new RequestCallback<Response>() {
                            @Override
                            public void onSuccess(Response response) {
                                getBasicUserInfo(cb);
                            }

                            @Override
                            public void onFailure(int error_code) {
                                cb.onFailure(error_code);
                            }
                        });
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                   defineError(error, cb);
                }
            });
        } catch(UnsupportedEncodingException e) {
            Log.e(TAG, e.getLocalizedMessage() );
        }
    }

    private void createToken(String login, String password, final RequestCallback<Response> cb) {
        try {
            String lgnpsw = login+":"+password;
            String encodedAuthString = "Basic "+new String( Base64.encode( lgnpsw.getBytes("UTF-8"), Base64.NO_WRAP), "UTF-8" );
            mService.createToken(encodedAuthString, new TokenRequest(new String[]{TokenRequest.SCOPE_REPO}), new Callback<AuthorizationResult>() {
                @Override
                public void success(AuthorizationResult authorizationResult, Response response) {
                    mToken = authorizationResult.token;
                    writeToken();
                    cb.onSuccess(response);
                }

                @Override
                public void failure(RetrofitError error) {
                    defineError(error, cb);
                }
            });
        } catch(UnsupportedEncodingException e) {
            Log.e(TAG, e.getLocalizedMessage() );
        }
    }

   private void writeToken() {
        if(mToken!=null) {
            mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit().putString(VAR_TOKEN, mToken).commit();
        }
    }

   public void logout() {
        mUserInfo = null;
        mToken = null;
        mLastResponseHeaders = null;
        mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit().remove(VAR_TOKEN).commit();
   }

   public void getRepositoreisList(int perPage, int currentPage, final RequestCallback<List<Repository>> rc){
        mService.getRepositoriesList(perPage, currentPage, new Callback<List<Repository>>() {
            @Override
            public void success(List<Repository> repositories, Response response) {
                mLastResponseHeaders = response;
                rc.onSuccess(repositories);
            }

            @Override
            public void failure(RetrofitError error) {
               Log.e(TAG, String.valueOf(error));
            }
        });
    }

   public void getRepositoryCommits(String owner, String repository, int perPage, int currentPage, final RequestCallback<List<Commit>> cb) {
        mService.getRepositoryCommits(owner, repository, perPage, currentPage, new Callback<List<Commit>>() {
            @Override
            public void success(List<Commit> commitList, Response response2) {
                mLastResponseHeaders = response2;
                cb.onSuccess(commitList);
            }

            @Override
            public void failure(RetrofitError error) {
                defineError(error, cb);
            }
        });
   }

   public boolean hasLastResponseNextPage() {
        if(mLastResponseHeaders!=null) {
            List<Header> headerList = mLastResponseHeaders.getHeaders();
            for(Header header : headerList) {
                if(header.getName()!=null && header.getName().equals(HEADER_LINK)) {
                    String value = header.getValue();
                    return value.contains("rel=\"next\"");
                }
            }
        }
        return false;
   }


    public int getNextPageFromLastResponse() {
        if(mLastResponseHeaders!=null) {
            List<Header> headerList = mLastResponseHeaders.getHeaders();
            for(Header header : headerList) {
                if(header.getName()!=null && header.getName().equals(HEADER_LINK)) {
                    String value = header.getValue();
                    int pos = value.indexOf("rel=\"next\"");
                    if(pos>0) {
                        Pattern pattern = Pattern.compile("\\&page=(\\d)");
                        Matcher m = pattern.matcher(value.substring(0,pos));
                        if(m.find()) {
                            return Integer.parseInt(m.group(1));
                        }
                    }
                }
            }
        }
        return -1;
    }

    public boolean isTokenSet() {
        return (mToken!=null && !mToken.equals(""));
    }

    private void defineError(RetrofitError error, final RequestCallback cb) {
        int probableReason = ERR_CODE_UNKONWN_ERROR;

        if(error.getResponse()==null) {
            if(mContext != null) {
                if(!isNetworkAvailable()) {
                    probableReason = ERR_CODE_NO_INTERNET;
                }
            }
        } else {

            try {
                StringBuilder errorBody = new StringBuilder();
                InputStream inputStream = error.getResponse().getBody().in();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = br.readLine()) != null) {
                    errorBody.append(line);
                }

            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            } catch (NullPointerException e) {
                Log.e(TAG, "Probably no respone body. "+e.getLocalizedMessage());
            }
            probableReason = error.getResponse().getStatus();

        }

        if(probableReason==ERR_CODE_UNKONWN_ERROR) {
            checkServiceAvailability(cb);
        } else {
            cb.onFailure(probableReason);
        }

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
    }


    public void checkServiceAvailability(final RequestCallback cb) {

        new AsyncTask<Void,Void,Boolean>() {
            @Override
            public Boolean doInBackground(Void... params) {
                try {
                    return InetAddress.getByName(GitHubLoader.API_URL).isReachable(500);
                } catch (Exception e) {
                    Log.e(TAG, toString());
                    return false;
                }
            }

            @Override
            public void onPostExecute(Boolean isReachable) {
                if (isReachable) {
                    cb.onFailure(ERR_CODE_UNKONWN_ERROR);
                } else {
                    cb.onFailure(ERR_CODE_SERVICE_UNAVAILABLE);
                }
            }
        }.execute();

    }

    public User getCurrentUser() {
        return mUserInfo;
    }
}
