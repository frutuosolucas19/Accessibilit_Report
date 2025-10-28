// com.example.acessibilit_report.model.ImagemMeta.java
package com.example.acessibilit_report.model;

import java.io.Serializable;

public class Imagem implements Serializable {
    private Long id;
    private String filename;
    private String contentType;
    private Integer ordem;
    private Long tamanhoBytes;
    private String url;

    public Imagem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
    public Long getTamanhoBytes() { return tamanhoBytes; }
    public void setTamanhoBytes(Long tamanhoBytes) { this.tamanhoBytes = tamanhoBytes; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
