package com.example.recordapplicationcoderbyte.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recordapplicationcoderbyte.R;
import com.example.recordapplicationcoderbyte.adapters.RecordListAdapter;
import com.example.recordapplicationcoderbyte.room.AppDatabase;
import com.example.recordapplicationcoderbyte.room.RecordDao;
import com.example.recordapplicationcoderbyte.room.RecordEntity;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SavedFragment extends Fragment {

    RecordDao recordDao;
    AppDatabase db;
    RecyclerView recyclerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_saved, container, false);
        recyclerView = RootView.findViewById(R.id.recyclerView);


        db = AppDatabase.getInstance(getContext());
        recordDao = db.recordDao();

        return RootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        recordDao.getAllRecord().subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<RecordEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<RecordEntity> recordEntities) {
                        RecordListAdapter adapter = new RecordListAdapter(recordEntities);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }
}