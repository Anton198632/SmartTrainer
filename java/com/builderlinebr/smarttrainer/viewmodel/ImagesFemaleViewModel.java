package com.builderlinebr.smarttrainer.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.builderlinebr.smarttrainer.database.ImagesFemale;
import com.builderlinebr.smarttrainer.repository.local.ImagesFemaleRepository;

import java.util.List;

public class ImagesFemaleViewModel  extends AndroidViewModel {
    List<ImagesFemale> links;
    ImagesFemaleRepository imagesFemaleRepository;

    public ImagesFemaleViewModel(@NonNull Application application) {
        super(application);
        imagesFemaleRepository = new ImagesFemaleRepository(application);
    }

    public List<ImagesFemale> getImagesLink(int id){
        links =  imagesFemaleRepository.getLinks(id);
        return  links;
    }

    public List<ImagesFemale> getAllImages(){
        links =  imagesFemaleRepository.getAll();
        return  links;
    }
}
