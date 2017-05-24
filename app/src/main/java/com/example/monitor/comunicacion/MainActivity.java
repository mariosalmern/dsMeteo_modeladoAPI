package com.example.monitor.comunicacion;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    TextView tvPagina;
    TextView textView_Mañana;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvPagina=(TextView)this.findViewById(R.id.tvPagina);
        textView_Mañana=(TextView)this.findViewById(R.id.textView_Mañana);
    }

    public void cargar(View v){
        ComunicacionTask com=new ComunicacionTask();
        MañanaTask mañanaTask=new MañanaTask();
        com.execute("http://www.aemet.es/xml/municipios/localidad_09059.xml"); //Burgos
        mañanaTask.execute("http://www.aemet.es/xml/municipios/localidad_09059.xml"); //Burgos
    }


    private class ComunicacionTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String cadena="";
            try {
                URL url = new URL(params[0]);
                URLConnection con = url.openConnection();

                InputStream is = con.getInputStream();
                Document doc;
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                doc = builder.parse(is);

                //RECUPERAMOS LOS ELEMENTOS DEL API http://www.aemet.es/xml/municipios/localidad_09059.xml , EN ESTE CASO PARA BURGOS.
                NodeList listanombre = doc.getElementsByTagName("nombre");
                NodeList listaPrecip = doc.getElementsByTagName("prob_precipitacion");

                NodeList listaTempMax = doc.getElementsByTagName("maxima");
                NodeList listaTempMin = doc.getElementsByTagName("minima");

                NodeList listaDireccViento = doc.getElementsByTagName("direccion");
                NodeList listaVientoVelocidad = doc.getElementsByTagName("velocidad");


                //MUESTRA DE LA LOCALIDAD SELECCIONADA:
                cadena += "-Localidad: " + listanombre.item(0).getTextContent() + "\n";

                //CALCULAMOS LA MEDIA DE LAS PROBABILIDADES DE PRECIPITACIÓN, ASEGURANDONOS ASÍ QUE NO COJEMOS VALORES NULOS DE LOS DISTINTOS PERIODOS DE PROBABILIDAD.
                double media_prob_precipitacion = 0;
                for (int i = 0; i <= 6; i++) {
                    String valor = listaPrecip.item(i).getTextContent();
                    if (valor == null || valor.equals("")) {
                        valor = "0";
                    }
                    media_prob_precipitacion += Double.parseDouble(valor);
                }
                media_prob_precipitacion = media_prob_precipitacion / 7;

                //ASIGNAMOS LA MEDIA DE LAS PROB. DE PRECIPITACION AL TEXTVIEW.
                cadena += "-Precipitaciones:" +
                        media_prob_precipitacion + "% " +"\n";

                //ASIGNAMOS LA TEMPERATURA MAXIMA Y MINIMA AL TEXTVIEW.
                cadena += "-Temperaturas:" + "\n" +
                        "Máxima:  " +
                        listaTempMax.item(0).getTextContent() + "\n"+
                        "Mínima:  " +
                        listaTempMin.item(0).getTextContent() + "\n" ;


                //Asignamos el viento:

                cadena += "-Vientos:" + "\n" +
                        "Dirección:  " +
                        listaDireccViento.item(0).getTextContent() + "\n"+
                        "Velocidad:  " +
                        listaVientoVelocidad.item(0).getTextContent() + "\n" ;


            }
            catch(Exception ex){
                ex.printStackTrace();
            }
            return cadena;
        }

        @Override
        protected void onPostExecute(String result){

            tvPagina.setText(result);

        }

    }

    private class MañanaTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String cadena="";
            try {
                URL url = new URL(params[0]);
                URLConnection con = url.openConnection();

                InputStream is = con.getInputStream();
                Document doc;
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                doc = builder.parse(is);

                //RECUPERAMOS LOS ELEMENTOS DEL API http://www.aemet.es/xml/municipios/localidad_09059.xml , EN ESTE CASO PARA BURGOS.
                NodeList listanombre = doc.getElementsByTagName("nombre");
                NodeList listaPrecip = doc.getElementsByTagName("prob_precipitacion");

                NodeList listaTempMax = doc.getElementsByTagName("maxima");
                NodeList listaTempMin = doc.getElementsByTagName("minima");


                //MUESTRA DE LA LOCALIDAD SELECCIONADA:
                cadena += listanombre.item(0).getTextContent() + "\n";

                //CALCULAMOS LA MEDIA DE LAS PROBABILIDADES DE PRECIPITACIÓN, ASEGURANDONOS ASÍ QUE NO COJEMOS VALORES NULOS DE LOS DISTINTOS PERIODOS DE PROBABILIDAD.
                double media_prob_precipitacion_mañana = 0;
                for (int i = 7; i <= 13; i++) {
                    String valor = listaPrecip.item(i).getTextContent();
                    if (valor == null || valor.equals("")) {
                        valor = "0";
                    }
                    media_prob_precipitacion_mañana += Double.parseDouble(valor);
                }
                media_prob_precipitacion_mañana = media_prob_precipitacion_mañana / 7;

                //ASIGNAMOS LA MEDIA DE LAS PROB. DE PRECIPITACION AL TEXTVIEW.
                cadena += "Prob. LLuvia:" + //Aproxima la media: (long)
                        (long)(media_prob_precipitacion_mañana > 0 ? media_prob_precipitacion_mañana + 0.01 : media_prob_precipitacion_mañana - 0.01) + "% " +"\n";

                //ASIGNAMOS LA TEMPERATURA MAXIMA Y MINIMA AL TEXTVIEW.
                cadena += "Temperatura:" + "\n" +
                        "Máx:" +
                        listaTempMax.item(3).getTextContent() + "\n"+
                        "Mín:"   +
                        listaTempMin.item(3).getTextContent() + "\n" ;

            }
            catch(Exception ex){
                ex.printStackTrace();
            }
            return cadena;
        }

        @Override
        protected void onPostExecute(String result){

            textView_Mañana.setText(result);

        }

    }



}
