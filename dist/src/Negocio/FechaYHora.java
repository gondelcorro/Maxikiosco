package Negocio;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FechaYHora {

    public String fechaActual() {

        /*  Calendar calendario = new GregorianCalendar();
         int dia = calendario.get(Calendar.DAY_OF_MONTH);
         int mes = calendario.get(Calendar.MONTH);
         mes = mes + 1;
         int año = calendario.get(Calendar.YEAR);
         String fecha = dia + "-" + mes + "-" + año;
         return fecha;*/
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        return formateador.format(ahora);
    }

    public String horaActual() {

        // DateFormat dateFormat = new SimpleDateFormat("hh:mm");
        Calendar c1 = Calendar.getInstance();
        int hora = c1.get(Calendar.HOUR_OF_DAY) + 1;
        int min = c1.get(Calendar.MINUTE);
        int seg = c1.get(Calendar.SECOND);
        String horaActual = " " + hora + ":" + min + ":" + seg;
        return horaActual;
    }

    public String obtenerTurno() {

        Calendar c1 = Calendar.getInstance();
        int hora = c1.get(Calendar.HOUR_OF_DAY);
        int minutos = c1.get(Calendar.MINUTE);
        String turno = "";
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm"); // HH PARA FORMATO EN 24 HS Y hh PARA FORMATO 12 HS

            String hora1 = "07:00";
            String hora2 = "16:00";
            String horaNueva = hora + ":" + minutos;
            //System.out.println("HORA ACTUAL: " + horaNueva);
            Date date1, date2, dateNueva;
            date1 = dateFormat.parse(hora1);
            date2 = dateFormat.parse(hora2);
            dateNueva = dateFormat.parse(horaNueva);

            if (dateNueva.after(date1) && dateNueva.before(date2)) {
               // System.out.println("La hora " + horaNueva + " está entre " + hora1 + " y " + hora2);
                turno = "Mañana";
            } else {
                //System.out.println("La hora " + horaNueva + " no está entre " + hora1 + " y " + hora2);
                turno = "Tarde";
            }
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }
        return turno;

    }
}
