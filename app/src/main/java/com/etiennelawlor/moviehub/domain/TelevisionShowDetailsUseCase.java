package com.etiennelawlor.moviehub.domain;

import com.etiennelawlor.moviehub.data.repositories.models.ContentRatingDataModel;
import com.etiennelawlor.moviehub.data.repositories.models.TelevisionShowCreditDataModel;
import com.etiennelawlor.moviehub.data.repositories.models.TelevisionShowDataModel;
import com.etiennelawlor.moviehub.data.repositories.tv.TelevisionShowDataSourceContract;
import com.etiennelawlor.moviehub.domain.models.TelevisionShowDetailsDomainModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

/**
 * Created by etiennelawlor on 6/26/17.
 */

public class TelevisionShowDetailsUseCase implements TelevisionShowDetailsDomainContract.UseCase {

    // region Constants
    private static final String ISO_31661 = "US";
    // endregion

    // region Member Variables
    private final TelevisionShowDataSourceContract.Repository televisionShowRepository;
    // endregion

    // region Constructors
    public TelevisionShowDetailsUseCase(TelevisionShowDataSourceContract.Repository televisionShowRepository) {
        this.televisionShowRepository = televisionShowRepository;
    }
    // endregion

    // region MovieDetailsDomainContract.UseCase Methods
    @Override
    public Single<TelevisionShowDetailsDomainModel> getTelevisionShowDetails(int televisionShowId) {
        return Single.zip(
                televisionShowRepository.getTelevisionShow(televisionShowId),
                televisionShowRepository.getTelevisionShowCredits(televisionShowId),
                televisionShowRepository.getSimilarTelevisionShows(televisionShowId),
                televisionShowRepository.getTelevisionShowContentRatings(televisionShowId),
                (televisionShowDataModel, televisionShowCreditsDataModel, televisionShowsDataModel, televisionShowContentRatingsDataModel) -> {
                    List<TelevisionShowCreditDataModel> cast = new ArrayList<>();
                    List<TelevisionShowCreditDataModel> crew = new ArrayList<>();
                    List<TelevisionShowDataModel> similarTelevisionShows = new ArrayList<>();
                    String rating = "";

                    if(televisionShowCreditsDataModel!=null){
                        cast = televisionShowCreditsDataModel.getCast();
                    }

                    if(televisionShowCreditsDataModel!=null){
                        crew = televisionShowCreditsDataModel.getCrew();
                    }

                    if(televisionShowsDataModel!=null){
                        similarTelevisionShows = televisionShowsDataModel.getTelevisionShows();
                    }

                    if(televisionShowContentRatingsDataModel!=null){
                        List<ContentRatingDataModel> contentRatingDataModels = televisionShowContentRatingsDataModel.getContentRatings();
                        if(contentRatingDataModels != null && contentRatingDataModels.size() > 0){
                            for(ContentRatingDataModel contentRatingDataModel : contentRatingDataModels){
                                String iso31661 = contentRatingDataModel.getIso31661();
                                if(iso31661.equals(ISO_31661)){
                                    rating = contentRatingDataModel.getRating();
                                    break;
                                }
                            }
                        }
                    }

                    return new TelevisionShowDetailsDomainModel(televisionShowDataModel, cast, crew, similarTelevisionShows, rating);
                });
    }
    // endregion

}
