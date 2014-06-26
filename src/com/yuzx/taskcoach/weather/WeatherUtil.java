/**
 * 
 */
package com.yuzx.taskcoach.weather;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * @author yuzx
 *
 */
public class WeatherUtil {
	// ����Web Service�������ռ�
	static final String SERVICE_NS = "http://WebXml.com.cn/";
	// ����Web Service�ṩ�����URL
	static final String SERVICE_URL = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx";

	/**
	 * ����ݣ�������ʡ�ݺͳ�����Ϣ
	 * 
	 * @return
	 */
	public static List<String> getProvinceList()
	{

		// ��Ҫ���õķ�����(��ñ�����Ԥ��Web Services֧�ֵ��ޡ�������ʡ�ݺͳ�����Ϣ)
		String methodName = "getRegionProvince";
		// ����HttpTransportSE�������
		HttpTransportSE httpTranstation = new HttpTransportSE(SERVICE_URL);

		httpTranstation.debug = true;
		// ʹ��SOAP1.1Э�鴴��Envelop����
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		// ʵ����SoapObject����
		SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
		envelope.bodyOut = soapObject;
		// ������.Net�ṩ��Web Service���ֽϺõļ�����
		envelope.dotNet = true;
		try
		{
			// ����Web Service
			httpTranstation.call(SERVICE_NS + methodName, envelope);
			if (envelope.getResponse() != null)
			{
				// ��ȡ��������Ӧ���ص�SOAP��Ϣ
				SoapObject result = (SoapObject) envelope.bodyIn;
				SoapObject detail = (SoapObject) result.getProperty(methodName
						+ "Result");
				// ������������Ӧ��SOAP��Ϣ��
				return parseProvinceOrCity(detail);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * ����ʡ�ݻ�ȡ�����б�
	 * 
	 * @param province
	 * @return
	 */
	public static List<String> getCityListByProvince(String province)
	{

		// ��Ҫ���õķ�����(��ñ�����Ԥ��Web Services֧�ֵĳ�����Ϣ,����ʡ�ݲ�ѯ���м��ϣ�������)
		String methodName = "getSupportCityString";
		
		HttpTransportSE httpTranstation = new HttpTransportSE(SERVICE_URL);
		httpTranstation.debug = true;
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
		soapObject.addProperty("theRegionCode", province);
		envelope.bodyOut = soapObject;
		envelope.dotNet = true;
		try
		{
			// ����Web Service
			httpTranstation.call(SERVICE_NS + methodName, envelope);
			if (envelope.getResponse() != null)
			{
				// ��ȡ��������Ӧ���ص�SOAP��Ϣ
				SoapObject result = (SoapObject) envelope.bodyIn;
				SoapObject detail = (SoapObject) result.getProperty(methodName
						+ "Result");
				// ������������Ӧ��SOAP��Ϣ��
				return parseProvinceOrCity(detail);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;

	}

	private static List<String> parseProvinceOrCity(SoapObject detail)
	{
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < detail.getPropertyCount(); i++)
		{
			String str = detail.getProperty(i).toString();
			// ������ÿ��ʡ��
			result.add(str.split(",")[0]);
		}
		return result;
	}

	public static SoapObject getWeatherByCity(String cityName)
	{

		// ���ݳ��л�������Ʋ�ѯ���δ��������������������ڵ�����ʵ��������������ָ��
		String methodName = "getWeather";
		
		HttpTransportSE httpTranstation = new HttpTransportSE(SERVICE_URL);
		httpTranstation.debug = true;
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
		soapObject.addProperty("theCityCode", cityName);
		envelope.bodyOut = soapObject;
		envelope.dotNet = true;
		
		try
		{
			// ����Web Service
			httpTranstation.call(SERVICE_NS + methodName, envelope);
			if (envelope.getResponse() != null)
			{
				// ��ȡ��������Ӧ���ص�SOAP��Ϣ
				SoapObject result = (SoapObject) envelope.bodyIn;
				SoapObject detail = (SoapObject) result.getProperty(methodName
						+ "Result");
				// ������������Ӧ��SOAP��Ϣ��
				return detail;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

}
