package com.itheamc.hamroclassroom_teachers.callbacks;

import com.google.firebase.storage.UploadTask;

public interface StorageCallbacks {
    void onSuccess(String imageUrl);
    void onFailure(Exception e);
    void onCanceled();
    void onProgress(UploadTask.TaskSnapshot taskSnapshot);
}
