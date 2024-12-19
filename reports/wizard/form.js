$(document).ready(function () {
    // Function to validate form inputs
    function validateForm() {
        const population = $('#population').val();
        const genotypedData = $('#genotyped').val();
        const chipSelect = $('#chip').val();
        let isValid = true;
        let errorMessage = '';

        // Check if population is provided
        if (!population) {
            isValid = false;
            errorMessage += 'Population is missing. ';
        }

        // Check if genotyped data selection is provided
        if (!genotypedData) {
            isValid = false;
            errorMessage += 'Genotyped data selection is missing. ';
        } else if (genotypedData === 'yes' && !chipSelect) {
            isValid = false;
            errorMessage += 'Chip selection is missing. ';
        }

        // Display error message if invalid
        if (!isValid) {
            $('#errorMessage').html('<i class="fa fa-exclamation-triangle" aria-hidden="true"></i> ' + errorMessage).show();
        } else {
            $('#errorMessage').hide();
        }

        return isValid;
    }

    // Show or hide chip selection based on genotyped data input
    $('#genotyped').change(function () {
        if ($(this).val() === 'yes') {
            $('#chip').val('');
            $('#chipSelection').show();
        } else {
            $('#chip').val('');
            $('#chipSelection').hide();
        }
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
    });

    // Form submission validation
    $('#form').submit(function (event) {
        if (!validateForm()) {
            event.preventDefault(); // Prevent submission if validation fails
        }
    });

    // Initialize error message container
    $('#errorMessage').hide();
});
