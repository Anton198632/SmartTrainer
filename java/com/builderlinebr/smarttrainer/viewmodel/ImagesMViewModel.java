package com.builderlinebr.smarttrainer.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.builderlinebr.smarttrainer.database.ImagesM;
import com.builderlinebr.smarttrainer.repository.local.ImagesMRepository;

import java.util.List;

public class ImagesMViewModel  extends AndroidViewModel {
    List<ImagesM> links;
    ImagesMRepository imagesMRepository;

    public ImagesMViewModel(@NonNull Application application) {
        super(application);
        imagesMRepository = new ImagesMRepository(application);
    }

    public List<ImagesM> getImagesLink(int id){
        links =  imagesMRepository.getLinks(id);
        return  links;
    }

    public List<ImagesM> getAllImages(){
        links =  imagesMRepository.getAll();
        return  links;
    }
}
