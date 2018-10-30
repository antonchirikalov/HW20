package com.toennies.ci1429.app.model.scale;

/**
 * A full weight record. Containing (if available netto, brutto, tara and standard meter (Eichzaehler).
 * The unit for this WeightData is GRAM.
 * @author renkenh
 */
public final class WeightData
{
	
	public static final int NO_COUNTER = -1;


	private double netto  = 0;
	private boolean nettoValid = false;
	private double brutto = 0;
	private boolean bruttoValid = false;
	private double tara   = 0;
	private int counter   = NO_COUNTER;
	
	
	/**
	 * Returns the netto. If not set directly, the netto is computed from brutto - tara.
	 * @return the netto value of this record.
	 */
	public double getNetto()
	{
		if (this.nettoValid)
			return this.netto;
		return this.brutto - this.tara;
	}

	/**
	 * Set the netto directly.
	 * @param netto The netto
	 */
	public void setNetto(double netto)
	{
		this.netto = netto;
		this.nettoValid = true;
	}
	
	/**
	 * Returns the brutto. If not set directly, the brutto is computed from netto + tara.
	 * @return the brutto value of this record.
	 */
	public double getBrutto()
	{
		if (this.bruttoValid)
			return this.brutto;
		return this.netto + this.tara;
	}
	
	/**
	 * Set the brutto directly.
	 * @param brutto The brutto
	 */
	public void setBrutto(double brutto)
	{
		this.brutto = brutto;
		this.bruttoValid = true;
	}
	
	/**
	 * Returns the tara. If brutto and netto are set, the tara is computed by brutto - netto.
	 * Otherwise the direct tara value is returned - if not set: 0.
	 * @return the tara value of this record.
	 */
	public double getTara()
	{
		if (this.bruttoValid && this.nettoValid)
			return this.brutto - this.netto;
		return this.tara;
	}
	
	/**
	 * Set the tara. If not set, the tara is 0.
	 * @param tara The tara
	 */
	public void setTara(double tara)
	{
		this.tara = tara;
	}
	
	/**
	 * Returns the standard meter (counter). If not set {@link #NO_COUNTER} is returned.
	 * @return The standard meter if set, otherwise {@link #NO_COUNTER}.
	 */
	public int getCounter()
	{
		return this.counter;
	}
	
	/**
	 * Sets the standard meter for this record.
	 * @param counter The standard meter.
	 */
	public void setCounter(int counter)
	{
		this.counter = counter;
	}
	
	/**
	 * Returns whether at least netto or brutto is set. Otherwise, the other values cannot be computed.
	 * @return If the record contains data such that all values can be correctly computed (where needed).
	 */
	public boolean isValid()
	{
		return this.bruttoValid || this.nettoValid;
	}
}