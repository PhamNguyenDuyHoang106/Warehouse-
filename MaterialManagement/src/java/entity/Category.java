package entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class Category implements Serializable {
    private int category_id;
    private String category_name;
    private Integer parent_id;
    private Timestamp created_at;
    private int disable;
    private String status;
    private String description;
    private String priority;
    private String code; 

    
    public Category() {
    }

    public Category(int category_id, String category_name) {
        this.category_id = category_id;
        this.category_name = category_name;
    }
    
    public Category(int category_id, String category_name, Integer parent_id, Timestamp created_at, 
            int disable, String status, String description, String priority, String code) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.parent_id = parent_id;
        this.created_at = created_at;
        this.disable = disable;
        this.status = status;
        this.description = description;
        this.priority = priority;
        this.code= code;

    }
    
    // Getters
    public int getCategory_id() {
        return category_id;
    }
    
    public String getCategory_name() {
        return category_name;
    }
    
    public Integer getParent_id() {
        return parent_id;
    }
    
    public Timestamp getCreated_at() {
        return created_at;
    }
    
    public int getDisable() {
        return disable;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getPriority() {
        return priority;
    }
    
    // Setters
    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
    
    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
    
    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }
    
    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
    
    public void setDisable(int disable) {
        this.disable = disable;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

  
    

    @Override
    public String toString() {
        return "Category{" + "category_id=" + category_id + ", category_name=" + category_name + ", parent_id=" + parent_id + ", created_at=" + created_at + ", disable=" + disable + ", status=" + status + ", description=" + description + ", priority=" + priority + ", code=" + code + '}';
    }
   
} 