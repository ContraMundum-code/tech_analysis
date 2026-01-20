package com.techanalysis.dto;

import java.util.List;

public class NetworkData {
    private List<NetworkNode> nodes;
    private List<NetworkLink> links;

    public List<NetworkNode> getNodes() { return nodes; }
    public void setNodes(List<NetworkNode> nodes) { this.nodes = nodes; }
    public List<NetworkLink> getLinks() { return links; }
    public void setLinks(List<NetworkLink> links) { this.links = links; }
}
