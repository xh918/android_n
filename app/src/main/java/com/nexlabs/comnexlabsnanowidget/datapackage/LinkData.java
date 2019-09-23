package com.nexlabs.comnexlabsnanowidget.datapackage;

import java.util.ArrayList;
import java.util.List;

public class LinkData {
    List<String> githubLinks;

    public LinkData() {
        this.githubLinks = new ArrayList<>();
    }
    public void addLink(String githubLink){
        this.githubLinks.add(githubLink);
    }
    public List<String> getLinks(){
        return new ArrayList<>(githubLinks);
    }

}
