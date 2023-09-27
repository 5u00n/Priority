package dev.hembram.priority.Models;

public class TaskModel {
    String task_name, task_date, task_stats,task_limit,task_imp;

    public TaskModel(String task_name, String task_date, String task_stats, String task_limit, String task_imp) {
        this.task_name = task_name;
        this.task_date = task_date;
        this.task_stats = task_stats;
        this.task_limit = task_limit;
        this.task_imp = task_imp;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_date() {
        return task_date;
    }

    public void setTask_date(String task_date) {
        this.task_date = task_date;
    }

    public String getTask_stats() {
        return task_stats;
    }

    public void setTask_stats(String task_stats) {
        this.task_stats = task_stats;
    }

    public String getTask_limit() {
        return task_limit;
    }

    public void setTask_limit(String task_limit) {
        this.task_limit = task_limit;
    }

    public String getTask_imp() {
        return task_imp;
    }

    public void setTask_imp(String task_imp) {
        this.task_imp = task_imp;
    }
}
