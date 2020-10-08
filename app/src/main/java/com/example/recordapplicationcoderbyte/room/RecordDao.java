package com.example.recordapplicationcoderbyte.room;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface RecordDao {

    @Query("SELECT * FROM records")
    Single<List<RecordEntity>> getAllRecord();

    @Insert
    Completable insert(RecordEntity recordEntity);
}
