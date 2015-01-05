{
  "dataSet": "${dataset}",
  "period": "${period}",
  "orgUnit": "${organization}",
  "dataValues": [
<#list Query_19 as row1>
    {
      "dataElement": "AiPqHCbJQJ1",
      "categoryOptionCombo": "u2QXNMacZLt",
      "value": "${row1.v1}"
    },
    {
      "dataElement": "AiPqHCbJQJ1",
      "categoryOptionCombo": "UBdaznQ8DlT",
      "value": "${row1.v3}"
    },
    {
      "dataElement": "AiPqHCbJQJ2",
      "categoryOptionCombo": "KahybAysMCQ",
      "value": "${row1.v6}"
    },
</#list>
<#list Query_20 as row2>
    {
    "dataElement": "AiPqHCbJQJ1",
    "categoryOptionCombo": "DA2N93v7s0O",
    "value": "${row2.v2}"
    },
    {
    "dataElement": "AiPqHCbJQJ2",
    "categoryOptionCombo": "tSwmrlTW11V",
    "value": "${row2.v4}"
    },
    {
    "dataElement": "AiPqHCbJQJ2",
    "categoryOptionCombo": "GYRYyntlK7n",
    "value": "${row2.v5}"
    }
</#list>
  ]
}