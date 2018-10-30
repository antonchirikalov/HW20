package com.toennies.ci1429.app.util;

/**
 * Contains result data of a cups print job. Bases on
 * org.cups4j.PrintRequestResult. This class is better because it's a pojo.
 */
public class CupsPrintResult {

	private final int jobid;
	private final String resultCode;
	private final String resultDescription;
	private final boolean successfulResult;

	public CupsPrintResult(int jobid, String resultCode, String resultDescription, boolean successfulResult) {
		this.jobid = jobid;
		this.resultCode = resultCode;
		this.resultDescription = resultDescription;
		this.successfulResult = successfulResult;
	}

	public int getJobid() {
		return jobid;
	}

	public String getResultCode() {
		return resultCode;
	}

	public String getResultDescription() {
		return resultDescription;
	}

	public boolean isSuccessfulResult() {
		return successfulResult;
	}

	@Override
	public String toString() {
		return "CupsPrintResult [jobid=" + jobid + ", resultCode=" + resultCode + ", resultDescription="
				+ resultDescription + ", successfulResult=" + successfulResult + "]";
	}

}
