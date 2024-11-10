package com.localvault.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Entity
@Table(name = "STORAGE")
@IdClass(StorageEntityID.class)
public abstract class StorageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;
    @Column(name = "name")
    protected String name;
    @Column(name = "parentDir")
    protected String parentDir;
    @Column(name = "lastModifiedDate")
    protected String lastModifiedDate;

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getParentDir() {
        return this.parentDir;
    }

    public String getlastModifiedDate() {
        return this.lastModifiedDate;
    }

    public String getAbsolutePath(){
        return this.parentDir + '/' + this.name;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setParentDir(String parentDir) {
        this.parentDir = parentDir;
    }

    public void setlastModifiedDate(String lastModifiedDateDate) {
        this.lastModifiedDate = lastModifiedDateDate;
    }

    public String toString(){
        return this.id + ", " + this.name + ", " + this.parentDir + ", " + lastModifiedDate + "\n";
    }
}
