$(document).ready(function () {
   // Function to check form validity
   function checkFormValidity() {
       const population = $('#population').val();
       const genotypedData = $('#genotyped').val();
       const chipSelect = $('#chip').val();

       // Enable submit if all required fields are filled
       if (population && genotypedData && (genotypedData === 'no' || chipSelect)) {
           $('#submitButton').prop('disabled', false);
       } else {
           $('#submitButton').prop('disabled', true);
       }
   }

   // Monitor changes and inputs
   $('#population, #genotyped, #chip').change(checkFormValidity);
   $('#genesInput, #snpsInput').on('input', checkFormValidity);

   // Show chip selection if "Yes" for genotyped data
   $('#genotyped').change(function () {
       if ($(this).val() === 'yes') {
           $('#chip').val('');
           $('#chipSelection').show();
       } else {
           $('#chip').val('');
           $('#chipSelection').hide();
       }

       // Trigger validation after changing genotypedData
       checkFormValidity();
   });

   // Example button functionality
   $('#exampleButton').click(function () {
       $('#population').val('eur');
       $('#genesInput').val('BRCA1\nTP53');
       $('#snpsInput').val('rs2981582\nrs3803662\nrs889312');
       $('#pgsInput').val('PGS000004');
       $('#genotyped').val('yes');
       $('#chip').val('IO'); // Omni 2.5M selected
       $('#chipSelection').show();

       // Trigger validation after filling example values
       checkFormValidity();
   });

     // Example button for CAD Study
   $('#cadExampleButton').click(function () {
       $('#population').val('eur');
       $('#genesInput').val('LDLR\nAPOE\nLPA');
       $('#snpsInput').val('rs10455872\nrs1122608');
       $('#pgsInput').val('PGS000667');
       $('#genotyped').val('no');
       $('#chip').val('');
       $('#chipSelection').hide();
       // Trigger validation after filling example values
       checkFormValidity();
   });
});