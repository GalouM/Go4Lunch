package com.galou.go4lunch.models.api;

import android.content.Context;

import com.galou.go4lunch.api.UserHelper;
import com.galou.go4lunch.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by galou on 2019-05-02
 */
public class UserHelperUnitTest {
    private String uid;
    private String username;
    private String email;
    private String urlPhoto;
    private User user;
    @Mock
    private Context context;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        uid= "uid";
        username = "User Name";
        email = "email@user.com";
        urlPhoto = "http://url";
        when(context.getApplicationContext()).thenReturn(context);
        FirebaseApp.initializeApp(context);

        UserHelper.createUser(uid, username, email, urlPhoto);
    }

    @Test
    public void userCreated_sameGet(){
        UserHelper.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                assertEquals(user.getUsername(), username);
            }
        });
    }
}
