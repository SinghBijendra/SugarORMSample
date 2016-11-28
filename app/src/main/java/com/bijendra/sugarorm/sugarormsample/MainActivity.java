package com.bijendra.sugarorm.sugarormsample;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bijendra.sugarorm.sugarormsample.model.Book;
import com.orm.SugarRecord;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //local variables
    List<Book> books =null;
    ListView mList;
    MyBookAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList= (ListView) findViewById(R.id.list);
        books = Book.listAll(Book.class);
        adapter=new MyBookAdapter();
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showOperationDialog(position);
            }
        });
    }


    /**
     * This function is used to show update/delete dialog
     * @param position
     */
    private void showOperationDialog(final int position)
    {
        CharSequence operations[] = new CharSequence[] {getString(R.string.update),getString(R.string.delete)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_operation));
        builder.setItems(operations, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which==0)//UPDATE
                    showDialogToAddEditBook(position);
                else if(which==1)//DELETE
                      deleteBook(position);
            }
        });
        builder.show();
    }

    /**
     * This function is used to delete Book  item from database
     * @param position
     */
    private void deleteBook(int position)
    {
        Book book=books.get(position);
        book.delete();
        books.remove(position);
        adapter.notifyDataSetChanged();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_home,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_add)
        {
            showDialogToAddEditBook(-1);
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This function used to show  dialog to add/edit book
     * @param position
     */
    private void showDialogToAddEditBook(final int position)
    {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(promptsView);

        final EditText etTitle = (EditText) promptsView
                .findViewById(R.id.etTitle);
        final EditText etEdition = (EditText) promptsView
                .findViewById(R.id.etEdition);
        final EditText etWriter = (EditText) promptsView
                .findViewById(R.id.etWriter);
        if(position>-1)
        {
            Book book=books.get(position);
            etTitle.setText(book.title);
            etEdition.setText(book.edition);
            etWriter.setText(book.writer);

        }

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.dismiss();

                                addEditBook(position,etTitle.getText().toString(),etEdition.getText().toString(),etWriter.getText().toString());
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.dismiss();
                            }
                        });


        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    /**
     * This function is used to add/edit book detail in database and local collection
     * @param position
     * @param title
     * @param edition
     * @param writer
     */
    private void addEditBook(int position,String title, String edition, String writer)
    {
        if(position<0) {//add book
            Book book = new Book(title, edition,writer);
            book.save();
            books.add(0, book);
        }
        else// update book
        {
            Book bookOld=books.get(position);
            Book book= SugarRecord.findById(Book.class,bookOld.getId());

            book.title=title;
            book.edition=edition;
            book.writer=writer;
            book.save();

            books.remove(position);// REMOVE OLD BOOK ITEM
            books.add(position,book);// ADD NEW BOOK ITEM
        }
        adapter.notifyDataSetChanged();

    }

    /**
     * This adapter is used to show book list in ListView
     */
    class MyBookAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return books.size();
        }

        @Override
        public Object getItem(int i) {
            return books.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
           if(view==null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view= inflater.inflate(R.layout.row_list,viewGroup,false);
           }
            TextView mLabel= (TextView) view.findViewById(R.id.label);
           Book book =books.get(i);

           StringBuilder sb=new StringBuilder();
            sb.append(getString(R.string.title)+":"+book.title
                    +"\n"+getString(R.string.edition)+":"+book.edition
                    +"\n"+getString(R.string.writer)+":"+book.writer);
           mLabel.setText(sb.toString());
            mLabel.setTag(book.getId());


            return view;
        }
    }

}


