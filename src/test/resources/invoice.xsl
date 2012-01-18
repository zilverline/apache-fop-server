<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:date="http://exslt.org/dates-and-times"
                extension-element-prefixes="date">
  <xsl:import href="fop/functions/date/date.date.xsl"/>
  <xsl:output method="xml" indent="yes"/>
  <xsl:decimal-format name="euro" decimal-separator="," grouping-separator="."/>
  <xsl:template match="/invoice-record">
    <fo:root>
      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21cm" margin-top="2cm"
                               margin-bottom="2cm" margin-left="2cm" margin-right="2cm"
                               font-family="Calibri">
          <fo:region-body/>
          <fo:region-after region-name="invoice-footer" extent="5mm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="A4">
        <fo:static-content flow-name="invoice-footer" font-size="7pt">
          <fo:table>
            <fo:table-body>
              <fo:table-row>
                <fo:table-cell text-align="center">
                  <fo:block>
                    BANK <xsl:value-of select="creditor-bank-account-number"/>
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="center">
                  <fo:block>
                    KvK <xsl:value-of select="creditor-coc-number"/>
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="center">
                  <fo:block>
                    BTW <xsl:value-of select="creditor-vat-number"/>
                  </fo:block>
                </fo:table-cell>
                <xsl:if test="creditor-iban">
                  <fo:table-cell text-align="center">
                    <fo:block>
                      IBAN <xsl:value-of select="creditor-iban"/>
                    </fo:block>
                  </fo:table-cell>
                </xsl:if>
                <xsl:if test="creditor-bic">
                  <fo:table-cell text-align="center">
                    <fo:block>
                      BIC <xsl:value-of select="creditor-bic"/>
                    </fo:block>
                  </fo:table-cell>
                </xsl:if>
              </fo:table-row>
            </fo:table-body>
          </fo:table>
        </fo:static-content>
        <fo:flow flow-name="xsl-region-body" font-size="8pt">
          <fo:block space-after="1cm"></fo:block>

          <fo:table>
            <fo:table-column column-width="8.5cm"/>
            <fo:table-column column-width="8.5cm"/>

            <fo:table-body>
              <fo:table-row>
                <fo:table-cell text-align="left">
                  <fo:block>
                    <xsl:value-of select="recipient-company-name"/>
                  </fo:block>
                  <xsl:if test="recipient-attn">
                    <fo:block>
                      <xsl:value-of select="recipient-attn"/>
                    </fo:block>
                  </xsl:if>
                  <fo:block>
                    <xsl:value-of select="recipient-address-street"/>
                  </fo:block>
                  <fo:block><xsl:value-of select="recipient-address-postal-code"/>,
                    <xsl:value-of select="recipient-address-city"/>
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right">
                  <fo:block>
                    <xsl:value-of select="creditor-company-name"/>
                  </fo:block>
                  <fo:block>
                    <xsl:value-of select="creditor-address-street"/>
                  </fo:block>
                  <fo:block><xsl:value-of select="creditor-address-postal-code"/>,
                    <xsl:value-of select="creditor-address-city"/>
                  </fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-body>

          </fo:table>

          <fo:block space-before="2cm" space-after="2cm" text-align="center" font-size="20pt" font-weight="bold">
            FACTUUR
          </fo:block>

          <fo:table>
            <fo:table-column column-width="8.5cm"/>
            <fo:table-column column-width="8.5cm"/>

            <fo:table-body>
              <fo:table-row>
                <fo:table-cell text-align="left">
                  <fo:block>Factuurnummer:
                    <xsl:value-of select="invoice-number"/>
                  </fo:block>
                  <xsl:if test="invoice-payment-instructions">
                    <fo:block>Uw kenmerk:
                      <xsl:value-of select="invoice-payment-instructions"/>
                    </fo:block>
                  </xsl:if>
                </fo:table-cell>
                <fo:table-cell text-align="right">
                  <fo:block>Factuurdatum:
                    <xsl:value-of select="date:format-date(invoice-date, 'dd-MM-yyyy')"/>
                  </fo:block>
                  <fo:block>Vervaldatum:
                    <xsl:value-of select="date:format-date(invoice-due-date, 'dd-MM-yyyy')"/>
                  </fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-body>

          </fo:table>
          <fo:block space-after="1cm"></fo:block>
          <fo:table>
            <fo:table-column column-width="9cm"/>
            <fo:table-column column-width="1cm"/>
            <fo:table-column column-width="2cm"/>
            <fo:table-column column-width="3cm"/>
            <fo:table-column column-width="2cm"/>
            <fo:table-header font-weight="bold">
              <fo:table-row border-bottom="1px solid #eee">
                <fo:table-cell text-align="left" padding-bottom="2px">
                  <fo:block>Omschrijving</fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right" padding-bottom="2px">
                  <fo:block>Aantal</fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right" padding-bottom="2px">
                  <fo:block>Bedrag</fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right" padding-bottom="2px">
                  <fo:block>Totaal ex. BTW</fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right" padding-bottom="2px">
                  <fo:block>BTW</fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-header>
            <fo:table-body>
              <xsl:for-each select="line-item-records/line-item-record">
                <fo:table-row border-bottom="1px solid #eee">
                  <fo:table-cell text-align="left" padding-bottom="2px" padding-top="2px">
                    <fo:block>
                      <xsl:value-of select="description"/>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell text-align="right" padding-bottom="2px" padding-top="2px">
                    <fo:block>
                      <xsl:value-of select="quantity"/>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell text-align="right" padding-bottom="2px" padding-top="2px">
                    <fo:block>&#x20ac;
                      <xsl:value-of select="format-number(amount, '###.###,00', 'euro')"/>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell text-align="right" padding-bottom="2px" padding-top="2px">
                    <fo:block>&#x20ac;
                      <xsl:value-of select="format-number(total-amount-ex-vat, '###.###,00', 'euro')"/>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell text-align="right" padding-bottom="2px" padding-top="2px">
                    <fo:block><xsl:value-of select="format-number(vat, '###,00%', 'euro')"/>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </xsl:for-each>
            </fo:table-body>
          </fo:table>
          <fo:block space-after="1cm"></fo:block>
          <fo:table>
            <fo:table-column column-width="9cm"/>
            <fo:table-column column-width="4cm"/>
            <fo:table-column column-width="4cm"/>
            <fo:table-body>
              <fo:table-row>
                <fo:table-cell>
                  <fo:block></fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right" padding-bottom="2px" padding-top="2px">
                  <fo:block font-weight="bold">Subtotaal</fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right" padding-bottom="2px" padding-top="2px">
                  <fo:block>&#x20ac; <xsl:value-of select="format-number(invoice-total, '###.###,00', 'euro')"/>
                  </fo:block>
                </fo:table-cell>
              </fo:table-row>
              <fo:table-row>
                <fo:table-cell>
                  <fo:block></fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right" padding-bottom="2px" padding-top="2px" border-bottom="1px solid #eee">
                  <fo:block font-weight="bold">Totaal BTW</fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right" padding-bottom="2px" padding-top="2px" border-bottom="1px solid #eee">
                  <fo:block>&#x20ac; <xsl:value-of select="format-number(invoice-total-vat, '###.###,00', 'euro')"/></fo:block>
                </fo:table-cell>
              </fo:table-row>
              <fo:table-row>
                <fo:table-cell>
                  <fo:block></fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right" padding-bottom="2px" padding-top="2px">
                  <fo:block font-weight="bold">Totaal incl. BTW</fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right" padding-bottom="2px" padding-top="2px">
                  <fo:block>&#x20ac; <xsl:value-of select="format-number(invoice-total-incl-vat, '###.###,00', 'euro')"/></fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-body>
          </fo:table>
          <fo:block space-before="5cm">Gelieve het totaal bedrag van &#x20ac; <xsl:value-of select="format-number(invoice-total-incl-vat, '###.###,00', 'euro')"/> uiterlijk <xsl:value-of select="date:format-date(invoice-due-date, 'dd-MM-yyyy')"/> over te maken op
            bankrekeningnummer <xsl:value-of select="creditor-bank-account-number"/> onder vermelding van factuurnummer <xsl:value-of select="invoice-number"/>
          </fo:block>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
</xsl:stylesheet>