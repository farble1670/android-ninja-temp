package org.jtb.ninjatemp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

abstract class RequestTask extends AsyncTask<Void, Void, Response> {
  private static final String API_TOKEN = "GdkUE5vYt7jisC2ZA2Fg6AKxZIlE6YmusyjE1YhukGs";
  private static final String URL_BASE = "https://api.ninja.is/rest";

  private final String path;
  private final Class<? extends Response> responseType;
  private final Map<String,String> query = new HashMap<String,String>();

  protected RequestTask(String path, Class<? extends Response> responseType) {
    this.path = path;
    this.responseType = responseType;
  }

  protected RequestTask(String path, Class<? extends Response> responseType, Map<String,String> query) {
    this(path, responseType);
    this.query.putAll(query);
  }

  @Override
  protected Response doInBackground(Void... voids) {
    String endpoint = String.format("%s%s?user_access_token=%s", URL_BASE, path, API_TOKEN);
    for (String key: query.keySet()) {
      endpoint += String.format("&%s=%s", key, query.get(key));
    }

    BufferedReader in = null;
    try {
      URLConnection connection = new URL(endpoint).openConnection();
      in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

      String line;
      StringBuilder sb = new StringBuilder();

      while ((line = in.readLine()) != null) {
        sb.append(line);
      }

      Class[] argTypes = {String.class};
      Constructor constructor = responseType.getDeclaredConstructor(argTypes);
      Object[] arguments = {sb.toString()};
      Object instance = constructor.newInstance(arguments);
      return (Response) instance;

    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      return null;
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      return null;
    } catch (InstantiationException e) {
      e.printStackTrace();
      return null;
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      return null;
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
    }
  }

  @Override
  protected abstract void onPostExecute(Response response);
}
