package com.example.dadac.getfilepath;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * @ Create by dadac on 2018/9/27.
 * @Function: 当第一次创建表格表需要插入数据时在此写 sql 语句
 * @Return:
 */
@Table(name = "child_info", onCreated = "")
public class ChildInfo {
    /**
     * name = "id";  数据库表中的一个字段
     * isId = true;    是否主键
     * autoGen = true;   是否自动增长
     * property = "NOT NULL"   添加约束
     */
    @Column(name = "_id", isId = true, autoGen = true, property = "NOT NULL")
    private int id;

    @Column(name = "u_name")
    private String name;

    @Column(name = "u_salary")
    private double salary;

    @Override
    public String toString() {
        return "child_info{" + "_id " + id + " u_name" + name + "_salary" + salary+"}";
    }

    public ChildInfo() {
    }

    public ChildInfo(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
