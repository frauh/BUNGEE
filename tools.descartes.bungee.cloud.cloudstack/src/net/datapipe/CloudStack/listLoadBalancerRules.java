/*******************************************************************************
Copyright 2015 Andreas Weber, Nikolas Herbst

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*******************************************************************************/

package net.datapipe.CloudStack;

import java.util.HashMap;

import org.w3c.dom.Document;

public class listLoadBalancerRules {
  public static void main(String[] args) throws Exception {
    String secret = "ezTQdv-TEOLAj8f1u_2XBnHIpA6GMK_jZULHvPCr8KeVpdCsD8ZcOUz6kFkaigpzBUkalLsofTikd_cFfVHyNQ"; //System.getenv("ESTRAT_SECRET");
	String apikey = "ow6-UWbqwiieO-zFgSqbIdB1P8fAXjfWW_gBaKzFEyy9i7siOn9H4GePg7x1jjhHFaYf0yEjcE18DvrKA4wpDw"; //System.getenv("ESTRAT_APIKEY");
	String apiURL = "http://141.21.72.8:8080/client/api";
	
	CloudStackAPI  client = new CloudStackAPI(apiURL, secret, apikey);
    HashMap<String,String> options = CLI.args_to_options(args);
    //options.put("publicipid", "e955321d-0b17-48c1-8ca5-3f8fb9bf868d");
    Document iso_list = client.listLoadBalancerRules(options);

    String elements[] = {"id", "account", "algorithm", "state", "cidrlist"};

    CLI.printDocument(iso_list, "//loadbalancerrule", elements);
  }
}
