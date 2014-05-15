package mei.ricardo.pessoa.app.ui.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mei.ricardo.pessoa.app.R;

/**
 * Created by rpessoa on 14/05/14.
 */
public class FragmentTemperature extends Fragment
{

    String specialText;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState)
    {
        View view = inflater.inflate (R.layout.fragment_temperature, container,false);
        //TextView textView= (TextView) view.findViewById (R.id.my_frag_txt);
        //textView.setText (specialText);
        return view;
    }

    public String getSpecialText ()
    {
        return specialText;
    }

    public void setSpecialText (String specialText)
    {
        this.specialText = specialText;
    }



}