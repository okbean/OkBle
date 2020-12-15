package okble.demo.ui.about;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AboutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("author:Okbean\n" +
                "github:https://github.com/okbean/OkBle\n" +
                "email:okbean020@163.com");
    }

    public LiveData<String> getText() {
        return mText;
    }
}