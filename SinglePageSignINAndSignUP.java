

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText Email , Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        checkifUserisLoggedIn();

        Button tales = findViewById(R.id.btntoTales);
        Email = findViewById(R.id.etEmail);
        Password = findViewById(R.id.etPassword);


        tales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate()) {
                    SignUpUser();
                    }
                }

        });

    }

    private void checkifUserisLoggedIn(){

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null){

            startActivity(new Intent(MainActivity.this,Tales.class));
            finish();
        }

    }

    private void SigninUser(){

       String email =  Email.getText().toString().trim();
       String password = Password.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkEmailverification();
                        } else {
                            Snackbar.make(findViewById(android.R.id.content),"Sign-In Failed",Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void SignUpUser(){

        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("-------TAG-------", "createUserWithEmail:success");
                            sendVerifyMail();

                        } else {
                            Log.w("-------TAG------", "createUserWithEmail:failure", task.getException());
                            String taskException = task.getException().toString();
                            if(taskException.equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")){
                                SigninUser();
                            }
                        }
                    }
                });
    }

    private boolean validate() {

        boolean result = false;

        String email = Email.getText().toString();
        String password = Password.getText().toString();

        if(email.isEmpty() || password.isEmpty() ){
            Toast.makeText(MainActivity.this,"Enter All The Details",Toast.LENGTH_SHORT).show();
        }else if( password.length()<8) {
            Snackbar.make(findViewById(android.R.id.content),"Password must be atleast of 8 characters",Snackbar.LENGTH_LONG).show();
        }else {
                result = true;
            }

        return result;
    }

    private void sendVerifyMail(){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Snackbar.make(findViewById(android.R.id.content),"Verification mail has been sent to your E-mail, verify and Sign-in Again",Snackbar.LENGTH_LONG).show();
                }else{
                    Snackbar.make(findViewById(android.R.id.content),"Verification mail ot sent, Try Again",Snackbar.LENGTH_LONG).show();
                    Log.w("-------TAG------", "sendEmail:failure", task.getException());

                }
            }
        });

    }

    private void checkEmailverification(){

        FirebaseUser user = mAuth.getCurrentUser();

        boolean emailflag = user.isEmailVerified();

        if(emailflag){
            startActivity(new Intent(MainActivity.this,Tales.class));
            finish();
        }else{
            Snackbar.make(findViewById(android.R.id.content),"Please Verify your E-mail",Snackbar.LENGTH_LONG).show();
            mAuth.signOut();
        }
    }

}
