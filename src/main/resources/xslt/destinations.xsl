<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="preferredActivity" select="'randonnée'"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>Liste des Destinations</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 20px;
                    }
                    .destination {
                        padding: 15px;
                        margin: 10px 0;
                        border-radius: 5px;
                        border: 1px solid #ccc;
                    }
                    .destination.match {
                        background-color: #ffff99;
                    }
                    .destination.no-match {
                        background-color: #99ff99;
                    }
                    .destination h3 {
                        margin-top: 0;
                    }
                    .destination-info {
                        margin: 5px 0;
                    }
                </style>
            </head>
            <body>
                <h1>Liste des Destinations de Vacances</h1>
                <p><strong>Activité préférée de l'utilisateur:</strong> <xsl:value-of select="$preferredActivity"/></p>
                <p><em>(Fond jaune = correspond à l'activité préférée, Fond vert = ne correspond pas)</em></p>

                <xsl:apply-templates select="//destination">
                    <xsl:sort select="name" data-type="text" order="ascending"/>
                </xsl:apply-templates>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="destination">
        <div>
            <xsl:attribute name="class">
                <xsl:text>destination </xsl:text>
                <xsl:choose>
                    <xsl:when test="activity1 = $preferredActivity or activity2 = $preferredActivity">
                        <xsl:text>match</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>no-match</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>

            <h3><xsl:value-of select="name"/></h3>
            <div class="destination-info">
                <strong>Description:</strong> <xsl:value-of select="description"/>
            </div>
            <div class="destination-info">
                <strong>Durée:</strong> <xsl:value-of select="duration"/> jours
            </div>
            <div class="destination-info">
                <strong>Activités:</strong> <xsl:value-of select="activity1"/>, <xsl:value-of select="activity2"/>
            </div>
            <div class="destination-info">
                <strong>Budget:</strong> <xsl:value-of select="budget"/> €
            </div>
        </div>
    </xsl:template>

</xsl:stylesheet>
