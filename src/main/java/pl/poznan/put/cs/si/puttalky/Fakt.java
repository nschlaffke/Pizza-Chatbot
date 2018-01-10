package pl.poznan.put.cs.si.puttalky;

import java.util.List;

/** Author: agalawrynowicz<br>
 * Date: 19-Dec-2016 */

public class Fakt {
	
	private String nazwa;
	private String wartosc;

    public List<String> getDodatki()
    {
        return dodatki;
    }

    public void setDodatki(List<String> dodatki)
    {
        this.dodatki = dodatki;
    }

    public Fakt(String nazwa)
    {
        this.nazwa = nazwa;
    }

    private List<String> dodatki;
	public Fakt(){}
	
	public Fakt(String nazwa, String wartosc)
	{
		this.nazwa=nazwa;
		this.wartosc = wartosc;
	}

	
	public String getNazwa() {
        return this.nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getWartosc() {
        return this.wartosc;
    }

    public void setWartosc(String wartosc) {
        this.wartosc = wartosc;
    }
	

}
