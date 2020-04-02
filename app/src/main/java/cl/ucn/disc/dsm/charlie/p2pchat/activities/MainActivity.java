/*
 * MIT License
 *
 * Copyright (c) 2020 CharlieCondorcet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cl.ucn.disc.dsm.charlie.p2pchat.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cl.ucn.disc.dsm.charlie.p2pchat.entities.Message;
import cl.ucn.disc.dsm.charlie.p2pchat.room.services.MessageListAdapter;
import cl.ucn.disc.dsm.charlie.p2pchat.room.services.ProyectViewModel;
import cl.ucn.disc.dsm.charlie.p2pchat.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import javax.xml.transform.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The principal Activity.
 *
 * @author Charlie Condorcet.
 */
public class MainActivity extends AppCompatActivity {

  /**
   * The logger.
   */
  private static final Logger log = LoggerFactory.getLogger(Transformer.class);

  /**
   * ProyectViewModel to start the database instance.
   */
  private ProyectViewModel mProyectViewModel;

  /**
   * Code approved to enter a message.
   */
  public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //Show the data (messages) in the recycler view.
    RecyclerView recyclerView = findViewById(R.id.recyclerview);
    final MessageListAdapter adapter = new MessageListAdapter(this);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    try {
      //When your Activity first starts, the ViewModelProviders will create the ViewModel
      this.mProyectViewModel = new ViewModelProvider(this).get(ProyectViewModel.class);

      Intent intent=new Intent(this, LoginActivity.class);
      startActivity(intent);

    }catch (Exception e){
      log.warn("unknown error to instance the ProyectViewModel, information about: {}",e);
    }

    // add an observer for the LiveData returned by getAlphabetizedMessages().
    //The onChanged() method fires when the observed data changes and the activity is in the foreground.
    mProyectViewModel.getAllMessages().observe(this, new Observer<List<Message>>() {
      @Override
      public void onChanged(@Nullable final List<Message> messages) {
        // Update the cached copy of the words in the adapter.
        adapter.setMessages(messages);
      }
    });

    //Start NewMessageActivity when the user taps the FAB.
    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        try {
          Intent intent = new Intent(MainActivity.this, NewMessageActivity.class);
          startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
          log.info("Activity change started successfully!");
        }catch (Exception e){
          log.warn("The activity could not start, information about the problem: {}",e);
        }
      }

    });

  }

  private int cantMessages = 3;

  //If the activity returns with RESULT_OK, insert the returned word into the
  // database by calling the insert() method of the WordViewModel.
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
      Message message = new Message(cantMessages,
          data.getStringExtra(NewMessageActivity.EXTRA_REPLY),
          null,
          null,
          null,
          0);
      mProyectViewModel.insertMessage(message);
      log.info("Message added correctly!");
    } else {
      Toast.makeText(
          getApplicationContext(),
          R.string.empty_not_saved,
          Toast.LENGTH_LONG).show();
      log.warn("Problems adding message: Void Message");
    }
    cantMessages++;
  }

}


