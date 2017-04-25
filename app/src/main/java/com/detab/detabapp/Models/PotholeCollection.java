package com.detab.detabapp.Models;

import android.location.Location;
import android.os.AsyncTask;

import com.detab.detabapp.Providers.GetPotholesTask;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by thiago on 14/04/2017.
 */

public class PotholeCollection
{
    private volatile List<TRLPothole> _potholes;
    private double currentLat;
    private double currentLng;
    private LatLng lastUpdatePosion;

    public PotholeCollection()
    {
        _potholes = new ArrayList<>();
    }

    public PotholeCollection(double lat, double lng)
    {
        this();
        UpdateFromNetwork(lat, lng);
    }

    public List<TRLPothole> GetAll()
    {
        return _potholes;
    }

    public void ComputeDistance(double lat, double lng)
    {
        //Cada vez que onLocationChanged, chamamos este método. Então, quando declarar um novo pothole, estas variaveis estarão atualizadas.
        currentLat = lat;
        currentLng = lng;

        //TODO pensar em como otimizar isso, calculando somente para os mais proximso ao inves de para todos toda a vez
        for (TRLPothole item : _potholes)
        {
            Location.distanceBetween(lat, lng, item.Lat, item.Lng, item.results);
        }

        Collections.sort(_potholes, new Comparator<TRLPothole>()
        {
            @Override
            public int compare(TRLPothole p1, TRLPothole p2)
            {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return p1.GetDistance() > p2.GetDistance() ? 1 : (p1.GetDistance() < p2.GetDistance()) ? -1 : 0;
            }
        });
    }

    public List<TRLPothole> GetNearPotholes()
    {
        List<TRLPothole> nearPotholes = new ArrayList<>();

        for (TRLPothole x : _potholes)
        {
            if (!x.wasNotified && x.results[0] < 15.0)
            {
                nearPotholes.add(x);
            }
        }

        /* A LISTA GERAL JÁ ESTÁ ORDENADA AGORA */

//        Collections.sort(nearPotholes, new Comparator<TRLPothole>()
//        {
//            @Override
//            public int compare(TRLPothole a, TRLPothole b)
//            {
//                if (a.results[0] < b.results[0]) return -1;
//                if (a.results[0] > b.results[0]) return 1;
//                return 0;
//            }
//        });

        return nearPotholes;
    }

    private List<TRLPothole> UpdateFromNetwork(double lat, double lng)
    {
        currentLat = lat;
        currentLng = lng;

        if (_potholes == null)
        {
            GetPotholesTask j = new GetPotholesTask(lat, lng);
            //j.delegate = this;
            AsyncTask<String, Void, List<TRLPothole>> a = j.execute("http://educandoomundo.tk/api/pothole");
            try
            {
                lastUpdatePosion = new LatLng(lat, lng);
                //_potholes = a.get();
                UpdateInternalPotholeList(a.get());
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            } catch (ExecutionException e)
            {
                e.printStackTrace();
            }
        }
        return _potholes;
    }

    public void CheckIfMustUpdate(double lat, double lng)
    {
        //API RETORNA BURACOS NO RADIO DE 1KM
        //ENTÃO SE PASSARMOS DE 1KM DESDE DE O ULTIMO UPDATE, TEMOS QUE CHAMAR A API NOVAMENTE
        // na verdade, menos ?? pois quando cruzarmos 1KM já temos que ter os novos pontos.
        //acho que vou por 900metros
        //No futuro terei que cuidar pois o lastUpdatePosition será atualizado tb por tempo e não só por distancia,
        //pois devemos ficar cientes de novos buracos que outros motoristas notificaram(ão)

        float[] r = new float[3];
        Location.distanceBetween(lastUpdatePosion.latitude, lastUpdatePosion.longitude, lat, lng, r);
        if (r[0] >= 900)
            UpdateFromNetwork(lat, lng);
    }

    private void UpdateInternalPotholeList(List<TRLPothole> list)
    {
        for (TRLPothole p : list)
        {
            //if(_potholes.stream().anyMatch(x -> x.Lat == p.Lat && x.Lng == p.Lng))
            boolean exists = false;
            for (TRLPothole x : _potholes)
                if (x.Lat == p.Lat && x.Lng == p.Lng)
                    exists = true;
            if (!exists)
                _potholes.add(p);
        }
    }

    public void DeclareNewPothole(double lat, double lng)
    {
        TRLPothole pothole = new TRLPothole(lng, lat);
        Location.distanceBetween(currentLat, currentLng, lat, lng, pothole.results);
        _potholes.add(pothole);
    }
}
