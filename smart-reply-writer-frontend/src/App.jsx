import { 
  Container, 
  TextField, 
  Typography,
  Box,
  Select,
  InputLabel,
  MenuItem,
  FormControl,
  Button, 
  CircularProgress 
} from '@mui/material';
import './App.css';
import { useState } from 'react';
import axios from 'axios';   // ✅ Import axios
import { red } from '@mui/material/colors';

function App() {

  const [emailContent, setEmailContent] = useState('');
  const [tone, setTone] = useState('');
  const [loading, setLoading] = useState(false);
  const [generatedReply, setGeneratedReply] = useState('');

  const handleSubmit = async () => {
    setLoading(true);
    try {
      const response = await axios.post("http://localhost:8085/api/smart/reply/generate", {
        emailContent,
        tone
      });

      setGeneratedReply(
        typeof response.data === 'string'
          ? response.data
          : JSON.stringify(response.data, null, 2) // pretty print JSON
      );

    } catch (error) {
      console.error("Error generating reply:", error);
      setGeneratedReply(" Failed to generate reply. Check server logs.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="md" sx={{ py: 4}}>
      <Typography variant="h4" component="h1" gutterBottom color='purple'>
       Email Reply Prompt using Gemini 2.5
      </Typography>

      {/* Input Section */}
      <Box sx={{ mx: 3 }}>
        <TextField
          fullWidth
          multiline
          rows={6}
          variant="outlined"
          label="Original Email Content"
          value={emailContent}
          onChange={(e) => setEmailContent(e.target.value)}
          sx={{ mb: 2 }}
        />

        <FormControl fullWidth sx={{ mb: 2 }}>
          <InputLabel>Tone (Optional)</InputLabel>
          <Select
            value={tone}
            label="Tone (Optional)"
            onChange={(e) => setTone(e.target.value)}
          >
            <MenuItem value="">None</MenuItem>
            <MenuItem value="professional">Professional</MenuItem>
            <MenuItem value="casual">Casual</MenuItem>
            <MenuItem value="slang">Slang</MenuItem>
          </Select>
        </FormControl>

        <Button sx={{ mb:3 }}
          variant="contained"
          onClick={handleSubmit}
          disabled={!emailContent || loading}
        >
          {loading ? <CircularProgress size={24} /> : "Generate Reply"}
        </Button>
      </Box>

      {/* Output Section */}
      <Box sx={{ mx:3 }}>

        <TextField
          fullWidth
          multiline
          rows={6}
          variant="outlined"
          value={generatedReply}
          InputProps={{ readOnly: true }}
          sx={{ mb: 2 }}
        />

        <Button 
          variant="outlined" 
          disabled={!generatedReply}
          onClick={() => navigator.clipboard.writeText(generatedReply)}
        >
          Copy to Clipboard
        </Button>
        
      </Box>
    </Container>
  );
}

export default App;
