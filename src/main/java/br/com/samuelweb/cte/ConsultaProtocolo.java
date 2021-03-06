package br.com.samuelweb.cte;

import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

import br.com.samuelweb.cte.exception.CteException;
import br.com.samuelweb.cte.util.CertificadoUtil;
import br.com.samuelweb.cte.util.ConstantesUtil;
import br.com.samuelweb.cte.util.ObjetoUtil;
import br.com.samuelweb.cte.util.WebServiceUtil;
import br.com.samuelweb.cte.util.XmlUtil;
import br.inf.portalfiscal.www.cte.wsdl.CteConsulta.CteConsultaStub;
import br.inf.portalfiscal.www.cte.wsdl.CteConsulta.CteConsultaStub.CteConsultaCTResult;

/**
 * Classe responsavel por Consultar a Situaçao do CTE na SEFAZ.
 * 
 * @author Samuel Oliveira - samuk.exe@hotmail.com - www.samuelweb.com.br
 * 
 */

public class ConsultaProtocolo {

	private static ConfiguracoesIniciaisCte configuracoesCte;
	private static CertificadoUtil certUtil;

	/**
	 * Classe Reponsavel Por Consultar o status da CTE na SEFAZ Versao 2.00
	 * 
	 * @param TConsSitCTe
	 * @return TRetConsSitCTe
	 * @throws CteException
	 */
	public static br.inf.portalfiscal.cte.schema_200.retConsSitCTe.TRetConsSitCTe consultar2(
			br.inf.portalfiscal.cte.schema_200.consSitCTe.TConsSitCTe consSitCTe, boolean valida) throws CteException {

		try {
			return XmlUtil.xmlToObject(consultar(XmlUtil.objectToXml(consSitCTe), valida).getExtraElement().toString(), br.inf.portalfiscal.cte.schema_200.retConsSitCTe.TRetConsSitCTe.class);
		} catch (JAXBException e) {
			throw new CteException(e.getMessage());
		}

	}

	/**
	 * Classe Reponsavel Por Consultar o status da CTE na SEFAZ Versao 3.00
	 * 
	 * @param TConsSitCTe
	 * @return TRetConsSitCTe
	 * @throws CteException
	 */
	public static br.inf.portalfiscal.cte.schema_300.retConsSitCTe.TRetConsSitCTe consultar3(
			br.inf.portalfiscal.cte.schema_300.consSitCTe.TConsSitCTe consSitCTe, boolean valida) throws CteException {
		
		try {
			return XmlUtil.xmlToObject(consultar(XmlUtil.objectToXml(consSitCTe), valida).getExtraElement().toString(), br.inf.portalfiscal.cte.schema_300.retConsSitCTe.TRetConsSitCTe.class);
		} catch (JAXBException e) {
			throw new CteException(e.getMessage());
		}
		
	}
	
	public static CteConsultaCTResult consultar(String xml, boolean valida) throws CteException {

		try {
			certUtil = new CertificadoUtil();
			configuracoesCte = ConfiguracoesIniciaisCte.getInstance();
			certUtil.iniciaConfiguracoes();
			
			if (valida) {
				String erros = Validar.validaXml(xml, ConstantesUtil.SERVICOS.CONSULTA_PROTOCOLO);
				if (!ObjetoUtil.isEmpty(erros)) {
					throw new CteException("Erro Na Validação do Xml: " + erros);
				}
			}

			System.out.println("Xml Consulta: " + xml);
			OMElement ome = AXIOMUtil.stringToOM(xml);

			CteConsultaStub.CteDadosMsg dadosMsg = new CteConsultaStub.CteDadosMsg();
			dadosMsg.setExtraElement(ome);

			CteConsultaStub.CteCabecMsg cteCabecMsg = new CteConsultaStub.CteCabecMsg();
			cteCabecMsg.setCUF(String.valueOf(configuracoesCte.getEstado().getCodigoIbge()));
			cteCabecMsg.setVersaoDados(configuracoesCte.getVersaoCte());

			CteConsultaStub.CteCabecMsgE cteCabecMsgE = new CteConsultaStub.CteCabecMsgE();
			cteCabecMsgE.setCteCabecMsg(cteCabecMsg);

			CteConsultaStub stub = new CteConsultaStub(
					WebServiceUtil.getUrl(ConstantesUtil.CTE, ConstantesUtil.SERVICOS.CONSULTA_PROTOCOLO));

			return stub.cteConsultaCT(dadosMsg, cteCabecMsgE);

		} catch (RemoteException | XMLStreamException e) {
			throw new CteException(e.getMessage());
		}

	}

}
