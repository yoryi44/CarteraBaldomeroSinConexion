/****
if (!input.isEmpty()) {

        if (input.length() < 3) {


        String newPrice2 = input;
        tvValorFragEfec.removeTextChangedListener(this); //To Prevent from Infinite Loop
        tvValorFragEfec.setText(newPrice2);
        tvValorFragEfec.setSelection(newPrice2.length()); //Move Cursor to end of String
        tvValorFragEfec.addTextChangedListener(this);


        } else if (input.length() == 3) {

        input = input.replace(",", "").replace(".", "");
        DecimalFormat formatoNumero = new DecimalFormat("###,###,##,#.#");

        String newPrice = formatoNumero.format(Double.parseDouble(input));
        newPrice = newPrice.replace(",", ".");

        if (newPrice.length() > 3) {
        newPrice = newPrice.substring(0, newPrice.length() - 2) + '.' + newPrice.substring(newPrice.length() - 1);
        }

        tvValorFragEfec.removeTextChangedListener(this); //To Prevent from Infinite Loop
        tvValorFragEfec.setText(newPrice);
        tvValorFragEfec.setSelection(newPrice.length()); //Move Cursor to end of String
        tvValorFragEfec.addTextChangedListener(this);



        } else if (input.length() == 8) {

        input = input.replace(",", "").replace(".", "");
        DecimalFormat formatoNumero = new DecimalFormat("###,###,###,###,#.#");

        String newPrice = formatoNumero.format(Double.parseDouble(input));
        newPrice = newPrice.replace(".", ",");

        if (newPrice.length() > 3) {
        newPrice = newPrice.substring(0, newPrice.length() - 2) + '.' + newPrice.substring(newPrice.length() - 1);
        }

        tvValorFragEfec.removeTextChangedListener(this); //To Prevent from Infinite Loop
        tvValorFragEfec.setText(newPrice);
        tvValorFragEfec.setSelection(newPrice.length()); //Move Cursor to end of String
        tvValorFragEfec.addTextChangedListener(this);


        } else if (input.length() == 10) {

        input = input.replace(",", "").replace(".", "");
        DecimalFormat formatoNumero = new DecimalFormat("###,###,###,###,#.#");

        String newPrice = formatoNumero.format(Double.parseDouble(input));
        newPrice = newPrice.replace(".", ",");

        if (newPrice.length() > 3) {
        newPrice = newPrice.substring(0, newPrice.length() - 2) + '.' + newPrice.substring(newPrice.length() - 1);
        }

        tvValorFragEfec.removeTextChangedListener(this); //To Prevent from Infinite Loop
        tvValorFragEfec.setText(newPrice);
        tvValorFragEfec.setSelection(newPrice.length()); //Move Cursor to end of String
        tvValorFragEfec.addTextChangedListener(this);


        } else if (input.length() == 12) {

        input = input.replace(",", "").replace(".", "");
        DecimalFormat formatoNumero = new DecimalFormat("###,###,###,###,#.#");

        String newPrice = formatoNumero.format(Double.parseDouble(input));
        newPrice = newPrice.replace(".", ",");

        if (newPrice.length() > 3) {
        newPrice = newPrice.substring(0, newPrice.length() - 2) + '.' + newPrice.substring(newPrice.length() - 1);
        }

        tvValorFragEfec.removeTextChangedListener(this); //To Prevent from Infinite Loop
        tvValorFragEfec.setText(newPrice);
        tvValorFragEfec.setSelection(newPrice.length()); //Move Cursor to end of String
        tvValorFragEfec.addTextChangedListener(this);


        } else {

        input = input.replace(",", "").replace(".", "");
        DecimalFormat formatoNumero = new DecimalFormat("###,###,###,##.#");

        String newPrice = formatoNumero.format(Double.parseDouble(input));
        newPrice = newPrice.replace(".", ",");
        if (newPrice.length() > 3) {
        newPrice = newPrice.substring(0, newPrice.length() - 3) + '.' + newPrice.substring(newPrice.length() - 2);
        }

        tvValorFragEfec.removeTextChangedListener(this); //To Prevent from Infinite Loop
        tvValorFragEfec.setText(newPrice);
        tvValorFragEfec.setSelection(newPrice.length()); //Move Cursor to end of String
        tvValorFragEfec.addTextChangedListener(this);

        }
        }
**/

/**   if (facCollection != null) {


 if (formaPago.parcial == true) {


 nroRecibo = clienteSel.consecutivo;

 listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo);
 listaFacturas4 = DataBaseBO.cargarIdPagoOGPendientes(nroRecibo);


 if (anticipo != null) {
 for (Facturas fac : listaFacturas2) {
 acert = fac.idPago;
 listaFacturas3.add(fac);
 }
 } else if (anticipo == null) {
 for (Facturas fac : listaFacturas2) {
 acert = fac.idPago;
 listaFacturas3.add(fac);
 }
 }

 if (anticipo != null) {
 for (Facturas fac : listaFacturas4) {
 acert = fac.idPago;
 listaFacturas3.add(fac);
 }
 } else if (anticipo == null) {
 for (Facturas fac : listaFacturas4) {
 acert = fac.idPago;
 listaFacturas3.add(fac);
 }
 }

 String str = "";

 for (int i = 0; i < listaFacturas3.size(); i++) {
 for (Facturas fruit : listaFacturas3) {
 str += "\'" + fruit.idPago + "\',";
 TotalFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str);
 DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str);
 TotalFormasPagoPEn = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str);
 DiferenciaFormasPagoPEN = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str);

 }
 }

 }

 if (formaPago.parcial == false) {

 nroRecibo = clienteSel.consecutivo;

 listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo);
 listaFacturas4 = DataBaseBO.cargarIdPagoOGPendientes(nroRecibo);


 if (anticipo != null) {
 for (Facturas fac : listaFacturas2) {
 acert = fac.idPago;
 listaFacturas3.add(fac);
 }
 } else if (anticipo == null) {
 for (Facturas fac : listaFacturas2) {
 acert = fac.idPago;
 listaFacturas3.add(fac);
 }
 }

 if (anticipo != null) {
 for (Facturas fac : listaFacturas4) {
 acert = fac.idPago;
 listaFacturas3.add(fac);
 }
 } else if (anticipo == null) {
 for (Facturas fac : listaFacturas4) {
 acert = fac.idPago;
 listaFacturas3.add(fac);
 }
 }

 String str = "";

 for (int i = 0; i < listaFacturas3.size(); i++) {
 for (Facturas fruit : listaFacturas3) {
 str += "\'" + fruit.idPago + "\',";
 TotalFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str);
 DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str);
 TotalFormasPagoPEn = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str);
 DiferenciaFormasPagoPEN = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str);

 }
 }
 }

 }**/

/**     if (TotalFormasPago != formaPago.valor) {

 if (lenguajeElegido == null) {

 } else if (lenguajeElegido != null) {
 if (lenguajeElegido.lenguaje.equals("USA")) {

 Toasty.warning(getApplicationContext(), "Total forms of payment must be equal to the amount collected").show();


 } else if (lenguajeElegido.lenguaje.equals("ESP")) {

 Toasty.warning(getApplicationContext(), "El total de formas de pago tiene que ser igual al monto del recaudo").show();


 }
 }

 }**/

/**       if (formaPago.valor - DiferenciaFormasPago != 0) {
 if (lenguajeElegido == null) {

 } else if (lenguajeElegido != null) {
 if (lenguajeElegido.lenguaje.equals("USA")) {

 Toasty.warning(getApplicationContext(), "The difference has to be 0 to finish the collection").show();


 } else if (lenguajeElegido.lenguaje.equals("ESP")) {

 Toasty.warning(getApplicationContext(), "La diferencia tiene que ser 0 para finalizar el recaudo").show();


 }
 }
 } **/

/***DataBaseBO.updateFormaPago(idPagoTransf, claseDocumento, sociedad, cod_cliente,
 cod_Vendedor, referenciaTransferencia, fechasDocumentos,
 fecha,
 precios, moneda,
 preciosfacturasLogicaTransfe,
 preciosParcialTrasnf,
 cuentBancariaTransferencia, moneda_Consig,
 NCF_Comprobante_fiscal, documentosFinanciero, consecutivo1,
 descripcion, "6",
 usuario, operacion_Cme, sincronizado, bancoTransferencia, "0",
 "0", fotos.idenFoto);***/