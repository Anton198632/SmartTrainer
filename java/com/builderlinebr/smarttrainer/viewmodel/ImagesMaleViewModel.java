package com.builderlinebr.smarttrainer.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.builderlinebr.smarttrainer.database.ImagesMale;
import com.builderlinebr.smarttrainer.repository.local.ImagesMaleRepository;

import java.util.List;

public class ImagesMaleViewModel  extends AndroidViewModel {

    List<ImagesMale> links;
    ImagesMaleRepository imagesMaleRepository;

    public ImagesMaleViewModel(@NonNull Application application) {
        super(application);
        imagesMaleRepository = new ImagesMaleRepository(application);
    }

    public List<ImagesMale> getImagesLink(int id){
        links =  imagesMaleRepository.getLinks(id);
        return  links;
    }

    public List<ImagesMale> getAllImages(){
        links =  imagesMaleRepository.getAll();
        return  links;
    }
}

